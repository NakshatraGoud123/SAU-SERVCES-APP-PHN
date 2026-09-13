package com.nisr.sauservices.data.model

data class HomeCategory(
    val id: String,
    val name: String,
    val description: String = "",
    val icon: String
)

data class HomeSubcategory(
    val id: String,
    val categoryId: String,
    val name: String
)

data class HomeProduct(
    val id: String,
    val subcategoryId: String,
    val name: String,
    val price: Int,
    val unit: String,
    val category: String,
    val imageRes: Int? = null,
    val shopId: String = "our_shop"
)

data class GroceryShop(
    val id: String,
    val name: String,
    val rating: Double,
    val distance: String,
    val deliveryTime: String,
    val offers: String,
    val imageRes: Int? = null,
    val isOpen: Boolean = true
)

object HomeEssentialsData {
    val shops = emptyList<GroceryShop>()
    val categories = emptyList<HomeCategory>()
    val subcategories = emptyList<HomeSubcategory>()
    val products = emptyList<HomeProduct>()
}
