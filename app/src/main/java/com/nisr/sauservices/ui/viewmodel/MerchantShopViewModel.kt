package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.Product
import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class MerchantShopUiState {
    data object Loading : MerchantShopUiState()
    data class Success(
        val vendor: Vendor,
        val products: List<Product>
    ) : MerchantShopUiState()
    data class Error(val message: String) : MerchantShopUiState()
}

class MerchantShopViewModel(
    private val repository: SupabaseRepository = SupabaseRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<MerchantShopUiState>(MerchantShopUiState.Loading)
    val uiState: StateFlow<MerchantShopUiState> = _uiState.asStateFlow()

    private var currentVendor: Vendor? = null
    private var allProductsList = listOf<Product>()

    fun loadShopData(vendorId: String) {
        viewModelScope.launch {
            _uiState.value = MerchantShopUiState.Loading
            try {
                val vendorResult = repository.getVendorDetails(vendorId)
                val productsResult = repository.getProductsByVendor(vendorId)

                val vendor = vendorResult.getOrNull()
                if (vendor == null) {
                    _uiState.value = MerchantShopUiState.Error(vendorResult.exceptionOrNull()?.message ?: "Vendor not found")
                    return@launch
                }

                currentVendor = vendor
                allProductsList = productsResult.getOrDefault(emptyList())
                _uiState.value = MerchantShopUiState.Success(vendor, allProductsList)
            } catch (e: Exception) {
                _uiState.value = MerchantShopUiState.Error(e.message ?: "An unexpected error occurred")
            }
        }
    }

    fun searchProducts(query: String) {
        val vendor = currentVendor ?: return
        if (query.isBlank()) {
            _uiState.value = MerchantShopUiState.Success(vendor, allProductsList)
        } else {
            val filtered = allProductsList.filter { 
                it.name.contains(query, ignoreCase = true) 
            }
            _uiState.value = MerchantShopUiState.Success(vendor, filtered)
        }
    }
}
