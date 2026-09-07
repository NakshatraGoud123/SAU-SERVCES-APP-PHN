package com.nisr.sauservices.data.model

import com.nisr.sauservices.R

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
    val shops = listOf(
        GroceryShop(
            id = "our_shop",
            name = "SAU Premium Mart",
            rating = 4.9,
            distance = "0.8 km",
            deliveryTime = "15-20 min",
            offers = "Flat 20% OFF",
            imageRes = R.drawable.essential_supplies, // Standard: shop_mart
            isOpen = true
        ),
        GroceryShop(
            id = "shop_2",
            name = "Fresh Basket",
            rating = 4.5,
            distance = "2.1 km",
            deliveryTime = "30-40 min",
            offers = "FREE Delivery",
            imageRes = R.drawable.food_beverages, // Standard: shop_fresh
            isOpen = false
        ),
        GroceryShop(
            id = "shop_3",
            name = "Daily Essentials",
            rating = 4.2,
            distance = "3.5 km",
            deliveryTime = "45-50 min",
            offers = "10% OFF on Staples",
            imageRes = R.drawable.homescreen_illustration, // Standard: shop_daily
            isOpen = true
        )
    )

    val categories = listOf(
        HomeCategory("veg_fruits", "Fruits & Vegetables", "Fresh & Organic", "🍎"),
        HomeCategory("dairy", "Dairy & Bakery", "Milk, Bread, Eggs & more", "🥛"),
        HomeCategory("staples", "Staples", "Rice, Atta, Oils & more", "🌾"),
        HomeCategory("snacks", "Snacks & Beverages", "Chips, Juices & more", "🍿"),
        HomeCategory("personal", "Personal Care", "Bath, Hair, Skincare", "🧼"),
        HomeCategory("household", "Household", "Cleaning essentials", "🧹")
    )

    val subcategories = listOf(
        // Fruits & Vegetables
        HomeSubcategory("all_vf", "veg_fruits", "All"),
        HomeSubcategory("fruits", "veg_fruits", "Fruits"),
        HomeSubcategory("veg", "veg_fruits", "Vegetables"),
        HomeSubcategory("organic", "veg_fruits", "Organic"),

        // Dairy & Bakery
        HomeSubcategory("all_db", "dairy", "All"),
        HomeSubcategory("milk", "dairy", "Milk"),
        HomeSubcategory("bread", "dairy", "Bread"),
        HomeSubcategory("eggs", "dairy", "Eggs")
    )

    val products = listOf(
        HomeProduct("p1", "fruits", "Tomato", 25, "1 kg", "Fruits & Vegetables", R.drawable.ic_apple), // Standard: prd_tomato
        HomeProduct("p2", "veg", "Potato", 20, "1 kg", "Fruits & Vegetables", R.drawable.ic_apple), // Standard: prd_potato
        HomeProduct("p3", "veg", "Onion", 28, "1 kg", "Fruits & Vegetables", R.drawable.ic_apple), // Standard: prd_onion
        HomeProduct("p4", "fruits", "Apple (Red)", 120, "500 g", "Fruits & Vegetables", R.drawable.ic_apple), // Standard: prd_apple
        HomeProduct("p5", "fruits", "Banana", 40, "1 Dozen", "Fruits & Vegetables", R.drawable.ic_apple), // Standard: prd_banana
        HomeProduct("p6", "veg", "Cucumber", 30, "1 kg", "Fruits & Vegetables", R.drawable.ic_apple), // Standard: prd_cucumber
        
        HomeProduct("d1", "milk", "Amul Milk", 30, "500 ml", "Dairy & Bakery", R.drawable.food_beverages), // Standard: prd_milk_amul
        HomeProduct("d2", "bread", "Brown Bread", 45, "400 g", "Dairy & Bakery", R.drawable.food_beverages), // Standard: prd_bread
        
        // Milk specific products for local vendor example
        HomeProduct("m1", "milk", "Fresh Buffalo Milk", 65, "1 L", "Dairy & Bakery", R.drawable.food_beverages, shopId = "our_shop"), // Standard: prd_milk_buffalo
        HomeProduct("m2", "milk", "Organic Cow Milk", 80, "1 L", "Dairy & Bakery", R.drawable.food_beverages, shopId = "our_shop"), // Standard: prd_milk_cow
        HomeProduct("m3", "milk", "Heritage Toned Milk", 28, "500 ml", "Dairy & Bakery", R.drawable.food_beverages, shopId = "shop_3") // Standard: prd_milk_toned
    )
}
