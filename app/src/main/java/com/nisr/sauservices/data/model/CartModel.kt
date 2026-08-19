package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import java.util.UUID

fun String.toSafeUuid(): String {
    return try {
        UUID.fromString(this)
        this
    } catch (e: Exception) {
        UUID.nameUUIDFromBytes(this.toByteArray()).toString()
    }
}

@Serializable
data class CartModel(
    @SerialName("id") val itemId: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("product_id") val productId: String = "",
    @SerialName("item_name") val itemName: String = "",
    @SerialName("price") val price: Double = 0.0,
    @SerialName("unit") val unit: String = "",
    @SerialName("quantity") val quantity: Int = 1,
    @SerialName("category") val category: String = "",
    @SerialName("subcategory") val subcategory: String = "",
    @SerialName("total_price") val totalPrice: Double = 0.0,
    @SerialName("date") val date: String? = null,
    @SerialName("time") val time: String? = null,
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class CartItemInsert(
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("item_name") val itemName: String,
    @SerialName("price") val price: Double,
    @SerialName("unit") val unit: String,
    @SerialName("quantity") val quantity: Int,
    @SerialName("category") val category: String,
    @SerialName("subcategory") val subcategory: String,
    @SerialName("total_price") val totalPrice: Double,
    @SerialName("date") val date: String? = null,
    @SerialName("time") val time: String? = null,
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis()
)
