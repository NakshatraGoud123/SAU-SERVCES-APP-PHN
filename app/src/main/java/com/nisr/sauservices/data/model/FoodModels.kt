package com.nisr.sauservices.data.model

data class Restaurant(
    val id: String,
    val name: String,
    val cuisine: String,
    val rating: Double,
    val deliveryTime: String,
    val costForTwo: String,
    val imageUrl: String = "",
    val imageRes: Int? = null,
    val offers: String = "",
    val categories: List<String> = emptyList()
)

data class FoodItem(
    val id: String,
    val restaurantId: String,
    val name: String,
    val description: String = "",
    val price: Int,
    val category: String, // e.g., Pizza, Sides, Beverages
    val isVeg: Boolean = true,
    val imageUrl: String = "",
    val imageRes: Int? = null
)
