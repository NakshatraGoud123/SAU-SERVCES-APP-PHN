package com.nisr.sauservices.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ResidentialServiceItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val subcategory: String,
    val durationMinutes: Int,
    val description: String = "Professional service by experts"
)

data class ResidentialCartItem(
    val service: ResidentialServiceItem,
    var quantity: Int
)

data class BookingDetails(
    val date: String = "",
    val timeSlot: String = "",
    val address: String = "",
    val phone: String = "",
    val paymentMethod: String = ""
)

data class ResidentialCategory(
    val id: String,
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val imageRes: Int? = null
)

data class ResidentialSubcategory(
    val id: String,
    val categoryId: String,
    val name: String
)
