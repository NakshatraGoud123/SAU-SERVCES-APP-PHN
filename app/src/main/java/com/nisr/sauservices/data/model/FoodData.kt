package com.nisr.sauservices.data.model

import com.nisr.sauservices.R

object FoodData {
    val restaurants = listOf(
        Restaurant(
            id = "res_1",
            name = "Pizza House",
            cuisine = "Italian, Pizza, Fast Food",
            rating = 4.5,
            deliveryTime = "30-40 mins",
            costForTwo = "₹499 for two",
            offers = "Flat 20% OFF",
            imageRes = R.drawable.food_beverages, // Standard: res_pizza
            categories = listOf("Pizza", "Sides", "Beverages", "Desserts")
        ),
        Restaurant(
            id = "res_2",
            name = "Biryani Hub",
            cuisine = "Biryani, Indian, Chinese",
            rating = 4.3,
            deliveryTime = "25-35 mins",
            costForTwo = "₹449 for two",
            offers = "Buy 1 Get 1 FREE",
            imageRes = R.drawable.food_beverages, // Standard: res_biryani
            categories = listOf("Main Course", "Starters", "Drinks")
        ),
        Restaurant(
            id = "res_3",
            name = "Burger Point",
            cuisine = "Burgers, Fast Food",
            rating = 4.4,
            deliveryTime = "20-30 mins",
            costForTwo = "₹399 for two",
            imageRes = R.drawable.food_beverages, // Standard: res_burger
            offers = "Free delivery"
        ),
        Restaurant(
            id = "res_4",
            name = "Healthy Bites",
            cuisine = "Salad, Healthy Food",
            rating = 4.2,
            deliveryTime = "30-45 mins",
            imageRes = R.drawable.food_beverages, // Standard: res_healthy
            costForTwo = "₹549 for two"
        )
    )

    val foodItems = listOf(
        // Pizza House
        FoodItem("fi_1", "res_1", "Margherita Pizza", "Classic cheese and tomato", 199, "Pizza", imageRes = R.drawable.food_beverages), // Standard: food_margherita
        FoodItem("fi_2", "res_1", "Farm House Pizza", "Fresh vegetables and herbs", 249, "Pizza", imageRes = R.drawable.food_beverages), // Standard: food_farmhouse
        FoodItem("fi_3", "res_1", "Cheesy Garlic Bread", "Baked with garlic and butter", 99, "Sides", imageRes = R.drawable.food_beverages), // Standard: food_garlic_bread
        FoodItem("fi_4", "res_1", "Coke (500ml)", "Refreshment", 40, "Beverages", imageRes = R.drawable.food_beverages), // Standard: food_coke
        
        // Biryani Hub
        FoodItem("fi_5", "res_2", "Chicken Biryani", "Special Hyderabadi Biryani", 299, "Main Course", false, imageRes = R.drawable.food_beverages), // Standard: food_biryani_chicken
        FoodItem("fi_6", "res_2", "Paneer Biryani", "Aromatic veg biryani", 249, "Main Course", imageRes = R.drawable.food_beverages), // Standard: food_biryani_paneer
        FoodItem("fi_7", "res_2", "Chicken 65", "Spicy deep fried chicken", 199, "Starters", false, imageRes = R.drawable.food_beverages) // Standard: food_chicken65
    )

    fun getRestaurantsByFilter(filter: String): List<Restaurant> {
        return when (filter) {
            "Top Rated" -> restaurants.filter { it.rating >= 4.4 }
            "Offers" -> restaurants.filter { it.offers.isNotEmpty() }
            else -> restaurants
        }
    }

    fun getMenuByRestaurant(restaurantId: String): List<FoodItem> {
        return foodItems.filter { it.restaurantId == restaurantId }
    }
}
