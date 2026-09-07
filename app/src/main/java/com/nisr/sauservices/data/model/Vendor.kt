package com.nisr.sauservices.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Vendor(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String,
    @SerialName("rating") val rating: Double = 4.5,
    @SerialName("distance") val distance: String = "1.0 km",
    @SerialName("delivery_time") val deliveryTime: String = "20-30 min",
    @SerialName("offers") val offers: String = "",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("is_open") val isOpen: Boolean = true,
    @SerialName("category") val category: String = "grocery"
)
