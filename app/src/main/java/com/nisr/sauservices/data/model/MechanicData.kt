package com.nisr.sauservices.data.model

import androidx.compose.ui.graphics.vector.ImageVector

data class MechanicCategory(val id: String, val name: String, val icon: ImageVector)
data class MechanicSubcategory(val id: String, val categoryId: String, val name: String)
data class MechanicServiceItem(
    val id: String,
    val name: String,
    val price: Double,
    val categoryId: String,
    val subcategoryId: String,
    val estimatedMinutes: Int
)

object MechanicData {
    val categories = emptyList<MechanicCategory>()
    val subcategories = emptyList<MechanicSubcategory>()
    val services = emptyList<MechanicServiceItem>()
}
