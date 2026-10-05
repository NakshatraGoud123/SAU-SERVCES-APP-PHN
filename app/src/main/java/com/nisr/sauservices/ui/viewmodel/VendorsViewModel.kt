package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class VendorsUiState {
    data object Loading : VendorsUiState()
    data class Success(val vendors: List<Vendor>) : VendorsUiState()
    data class Error(val message: String) : VendorsUiState()
}

class VendorsViewModel(
    private val repository: SupabaseRepository = SupabaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<VendorsUiState>(VendorsUiState.Loading)
    val uiState: StateFlow<VendorsUiState> = _uiState.asStateFlow()

    private var allVendorsList = listOf<Vendor>()

    init {
        fetchAllVendors()
    }

    fun fetchAllVendors() {
        fetchVendors(null)
    }

    fun fetchVendors(category: String? = null) {
        viewModelScope.launch {
            _uiState.value = VendorsUiState.Loading
            val result = repository.getVendors(category)
            if (result.isSuccess) {
                allVendorsList = result.getOrDefault(emptyList())
                _uiState.value = VendorsUiState.Success(allVendorsList)
            } else {
                _uiState.value = VendorsUiState.Error(result.exceptionOrNull()?.message ?: "Failed to load vendors")
            }
        }
    }

    fun searchVendors(query: String) {
        if (query.isBlank()) {
            _uiState.value = VendorsUiState.Success(allVendorsList)
        } else {
            val filtered = allVendorsList.filter { 
                it.displayName.contains(query, ignoreCase = true) || 
                it.displayCategory.contains(query, ignoreCase = true) 
            }
            _uiState.value = VendorsUiState.Success(filtered)
        }
    }
}
