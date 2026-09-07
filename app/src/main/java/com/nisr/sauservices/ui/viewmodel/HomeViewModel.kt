package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Category
import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
            
            val categoriesResult = repository.getCategories()
            val vendorsResult = repository.getVendors()

            if (categoriesResult.isSuccess) {
                _uiState.value = HomeUiState.Success(
                    categories = categoriesResult.getOrDefault(emptyList()),
                    vendors = vendorsResult.getOrDefault(emptyList())
                )
            } else {
                _uiState.value = HomeUiState.Error(
                    categoriesResult.exceptionOrNull()?.message ?: "Failed to fetch data"
                )
            }
        }
    }
}
