package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Category
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.data.model.ServiceModel
import com.nisr.sauservices.data.repository.SupabaseRepository
import com.nisr.sauservices.data.repository.RealtimeDatabaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val categories: List<Category> = emptyList(),
        val featuredServices: List<Category> = emptyList(),
        val vendors: List<Vendor> = emptyList()
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

data class QuickReorderItem(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val type: ReorderType
)

enum class ReorderType { VENDOR, SERVICE }

class HomeViewModel(
    private val repository: SupabaseRepository = SupabaseRepository(),
    private val realtimeRepository: RealtimeDatabaseRepository = RealtimeDatabaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val activeStatuses = listOf("placed", "accepted", "on_the_way", "arrived", "started")
    private val completedStatuses = listOf("completed", "delivered")

    val latestActiveOrder: StateFlow<OrderModel?> = realtimeRepository.observeUserActivity()
        .map { orders ->
            orders.filter { it.status.lowercase() in activeStatuses }
                .maxByOrNull { it.createdAt ?: "" }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _quickReorderItems = MutableStateFlow<List<QuickReorderItem>>(emptyList())
    val quickReorderItems: StateFlow<List<QuickReorderItem>> = _quickReorderItems.asStateFlow()

    init {
        fetchHomeData()
        observeHistory()
    }

    private fun observeHistory() {
        val uid = repository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            // Combine Orders and Bookings activity
            combine(
                realtimeRepository.observeUserActivity(),
                repository.observeMyBookings(uid)
            ) { orders, bookings ->
                val eligibleOrders = orders.filter { it.status.lowercase() in completedStatuses && !it.vendorId.isNullOrEmpty() }
                val eligibleBookings = bookings.filter { it.status.lowercase() == "completed" && it.serviceId.isNotEmpty() }
                
                // Pair them with type and date for sorting/deduplication
                val combinedHistory = (eligibleOrders.map { Triple(it.vendorId ?: "", ReorderType.VENDOR, it.createdAt ?: "") } +
                                     eligibleBookings.map { Triple(it.serviceId, ReorderType.SERVICE, it.createdAt ?: "") })
                                     .sortedByDescending { it.third }
                
                // Deduplicate by ID
                val uniqueHistory = mutableListOf<Pair<String, ReorderType>>()
                val seenIds = mutableSetOf<String>()
                
                for (item in combinedHistory) {
                    if (item.first !in seenIds) {
                        seenIds.add(item.first)
                        uniqueHistory.add(item.first to item.second)
                    }
                    if (uniqueHistory.size >= 5) break
                }
                uniqueHistory
            }.distinctUntilChanged()
            .collectLatest { uniqueItems ->
                if (uniqueItems.isEmpty()) {
                    _quickReorderItems.value = emptyList()
                    return@collectLatest
                }

                // Resolve details (Batch fetch)
                val vendorIds = uniqueItems.filter { it.second == ReorderType.VENDOR }.map { it.first }
                val serviceIds = uniqueItems.filter { it.second == ReorderType.SERVICE }.map { it.first }

                coroutineScope {
                    val vendorsTask = async { repository.getVendorsByIds(vendorIds) }
                    val servicesTask = async { repository.getServicesByIds(serviceIds) }

                    val vendors = vendorsTask.await().getOrDefault(emptyList())
                    val services = servicesTask.await().getOrDefault(emptyList())

                    val result = uniqueItems.mapNotNull { (id, type) ->
                        when (type) {
                            ReorderType.VENDOR -> {
                                vendors.find { it.id == id }?.let {
                                    QuickReorderItem(it.id, it.displayName, it.imageUrl, ReorderType.VENDOR)
                                }
                            }
                            ReorderType.SERVICE -> {
                                services.find { it.id == id }?.let {
                                    QuickReorderItem(it.id, it.name, it.imageUrl, ReorderType.SERVICE)
                                }
                            }
                        }
                    }
                    _quickReorderItems.value = result
                }
            }
        }
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                coroutineScope {
                    val categoriesTask = async { repository.getCategories() }
                    val vendorsTask = async { repository.getVendors() }

                    val categoriesResult = categoriesTask.await()
                    val vendorsResult = vendorsTask.await()

                    _uiState.value = HomeUiState.Success(
                        categories = categoriesResult.getOrDefault(emptyList()),
                        vendors = vendorsResult.getOrDefault(emptyList())
                    )
                }
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun refresh() = fetchHomeData()
}
