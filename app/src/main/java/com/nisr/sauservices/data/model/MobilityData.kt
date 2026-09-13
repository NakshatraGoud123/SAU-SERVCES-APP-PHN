package com.nisr.sauservices.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class MobilityServiceType(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val baseFare: Double,
    val perKmFare: Double,
    val description: String = ""
)

data class MobilityParcelCategory(
    val id: String,
    val name: String,
    val icon: ImageVector
)

object MobilityData {
    val serviceTypes = emptyList<MobilityServiceType>()
    val cabCategories = emptyList<MobilityServiceType>()
    val parcelCategories = emptyList<MobilityParcelCategory>()
}
