package com.nisr.sauservices.data.model

object FoodData {
    val restaurants = emptyList<Restaurant>()
    val foodItems = emptyList<FoodItem>()

    fun getRestaurantsByFilter(filter: String): List<Restaurant> = emptyList()
    fun getMenuByRestaurant(restaurantId: String): List<FoodItem> = emptyList()
}
