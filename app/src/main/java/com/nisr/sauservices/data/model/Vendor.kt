package com.nisr.sauservices.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vendor(
    @SerialName("id") val id: String = "",
    @SerialName("business_name") val name: String? = "Premium Shop",
    @SerialName("business_type") val category: String? = "Grocery",
    @SerialName("rating") val rating: Double? = 5.0,
    @SerialName("logo_url") val imageUrl: String? = null,
    @SerialName("is_active") val isOpen: Boolean? = true,
    @SerialName("address") val address: String? = null,
    @SerialName("latitude") val latitude: Double? = null,
    @SerialName("longitude") val longitude: Double? = null,
    @SerialName("description") val offers: String? = "",
    @SerialName("delivery_time") val deliveryTime: String? = "20 min",
    @SerialName("distance") val distance: String? = "1.0 km"
) {
    // UI Helpers to handle nulls safely
    val displayName: String get() = name ?: "Premium Shop"
    val displayCategory: String get() = category ?: "General"
    val displayRating: Double get() = rating ?: 5.0
    val isAvailable: Boolean get() = isOpen ?: true
}
