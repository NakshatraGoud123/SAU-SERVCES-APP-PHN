package com.nisr.sauservices.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class Order(
    val orderId: String,
    val customerName: String,
    val customerPhone: String = "+91 98765 00001",
    val amount: String = "₹0",
    val totalPrice: Double = 0.0,
    var status: String, // placed, accepted, ready, completed, rejected
    val paymentMode: String = "COD", // COD, Prepaid
    val items: List<OrderItem> = emptyList(),
    val createdAt: String = "2024-03-04 14:30",
    val cartAddedTime: String = "14:15",
    val address: String = ""
)

@Serializable
data class OrderItem(
    @SerialName("id") val id: String = "",
    @SerialName("order_id") val orderId: String = "",
    @SerialName("item_name") val name: String,
    @SerialName("quantity") val quantity: Int,
    @SerialName("price") val price: Double = 0.0
)
