package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Product
import com.nisr.sauservices.data.model.ServiceModel
import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

sealed class SearchUiState {
    data object Idle : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(
        val vendors: List<Vendor> = emptyList(),
        val products: List<Product> = emptyList(),
        val services: List<ServiceModel> = emptyList()
    ) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class SearchViewModel(
    private val repository: SupabaseRepository = SupabaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun performSearch(query: String) {
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                coroutineScope {
                    val vendorsTask = async { repository.searchVendors(query) }
                    val productsTask = async { repository.searchProducts(query) }
                    val servicesTask = async { repository.searchServices(query) }

                    val vRes = vendorsTask.await()
                    val pRes = productsTask.await()
                    val sRes = servicesTask.await()

                    _uiState.value = SearchUiState.Success(
                        vendors = vRes.getOrDefault(emptyList()),
                        products = pRes.getOrDefault(emptyList()),
                        services = sRes.getOrDefault(emptyList())
                    )
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(e.message ?: "Search failed")
            }
        }
    }
}
