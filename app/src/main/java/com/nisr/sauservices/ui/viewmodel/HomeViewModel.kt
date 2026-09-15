package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Category
import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class HomeViewModel(
    private val repository: SupabaseRepository = SupabaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchHomeData()
    }

    fun fetchHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            android.util.Log.d("HOME_DATA", "Starting to fetch home data...")
            
            try {
                coroutineScope {
                    val categoriesTask = async { repository.getCategories() }
                    val vendorsTask = async { repository.getVendors() }

                    val categoriesResult = categoriesTask.await()
                    val vendorsResult = vendorsTask.await()

                    val categories = categoriesResult.getOrDefault(emptyList())
                    val vendors = vendorsResult.getOrDefault(emptyList())

                    android.util.Log.d("HOME_DATA", "Loaded ${categories.size} categories and ${vendors.size} vendors")
                    
                    if (vendors.isEmpty()) {
                        android.util.Log.w("HOME_DATA", "WARNING: Vendors list is empty from Supabase!")
                    }
                    
                    _uiState.value = HomeUiState.Success(
                        categories = categories,
                        vendors = vendors
                    )
                }
            } catch (e: Exception) {
                android.util.Log.e("HOME_DATA", "Crash in fetchHomeData: ${e.message}")
                _uiState.value = HomeUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun refresh() = fetchHomeData()
}
