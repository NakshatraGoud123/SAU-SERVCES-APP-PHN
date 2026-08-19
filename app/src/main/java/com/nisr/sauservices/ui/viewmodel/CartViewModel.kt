package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.model.toSafeUuid
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.HomeProduct
import com.nisr.sauservices.data.repository.SupabaseRepository
import com.nisr.sauservices.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val cartRepository = CartRepository()
    private val repository = SupabaseRepository()

    private val _dbCartItems = MutableStateFlow<List<CartModel>>(emptyList())
    val dbCartItems = _dbCartItems.asStateFlow()

    private val _orderStatus = MutableStateFlow<Result<String>?>(null)
    val orderStatus = _orderStatus.asStateFlow()

    init {
        viewModelScope.launch {
            cartRepository.getCartItems().collect {
                _dbCartItems.value = it
            }
        }
    }

    fun addItemToCart(
        name: String,
        price: Double,
        category: String,
        subcategory: String,
        unit: String,
        productId: String,
        date: String? = null,
        time: String? = null,
        quantity: Int = 1,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        viewModelScope.launch {
            val userId = repository.getCurrentUserId() ?: run {
                onResult(Result.failure(Exception("User not logged in")))
                return@launch
            }

            val existingItem = _dbCartItems.value.find {
                it.productId == productId.toSafeUuid() && it.date == date && it.time == time
            }

            val result = if (existingItem != null) {
                cartRepository.updateQuantity(existingItem.itemId, existingItem.quantity + quantity)
            } else {
                val item = CartModel(
                    userId = userId,
                    itemName = name,
                    price = price,
                    category = category,
                    subcategory = subcategory,
                    unit = unit,
                    productId = productId.toSafeUuid(),
                    date = date,
                    time = time,
                    quantity = quantity,
                    totalPrice = price * quantity,
                )
                cartRepository.addToCart(item)
            }
            onResult(result)
        }
    }

    fun getHomeItemQuantity(productId: String): Int {
        val safeId = productId.toSafeUuid()
        return _dbCartItems.value.find { it.productId == safeId }?.quantity ?: 0
    }

    fun addHomeProduct(product: HomeProduct) {
        val safeId = product.id.toSafeUuid()
        _dbCartItems.value.find { it.productId == safeId }?.let { existingItem ->
            updateQuantity(existingItem.itemId, existingItem.quantity + 1)
        } ?: run {
            addItemToCart(
                name = product.name,
                price = product.price.toDouble(),
                category = product.category,
                subcategory = "", 
                unit = product.unit,
                productId = product.id,
            )
        }
    }

    fun removeHomeProduct(productId: String) {
        val safeId = productId.toSafeUuid()
        _dbCartItems.value.find { it.productId == safeId }?.let { existingItem ->
            if (existingItem.quantity > 1) {
                updateQuantity(existingItem.itemId, existingItem.quantity - 1)
            } else {
                viewModelScope.launch {
                    cartRepository.removeItem(existingItem.itemId)
                }
            }
        }
    }

    fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            if (quantity <= 0) {
                cartRepository.removeItem(itemId)
            } else {
                cartRepository.updateQuantity(itemId, quantity)
            }
        }
    }

    fun clearHomeCart() {
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }

    fun placeOrder(address: String, @Suppress("UNUSED_PARAMETER") paymentMethod: String = "Cash on Delivery") {
        viewModelScope.launch {
            val items = _dbCartItems.value
            if (items.isEmpty()) return@launch

            val total = items.sumOf { it.totalPrice }
            val order = OrderModel(
                userId = repository.getCurrentUserId() ?: "",
                items = items,
                totalAmount = total,
                address = address,
                status = "placed"
            )

            repository.placeOrder(order).onSuccess { orderId ->
                cartRepository.clearCart()
                _orderStatus.value = Result.success(orderId)
            }.onFailure {
                _orderStatus.value = Result.failure(it)
            }
        }
    }

    fun resetOrderStatus() {
        _orderStatus.value = null
    }
}
