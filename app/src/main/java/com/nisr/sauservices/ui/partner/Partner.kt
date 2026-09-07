package com.nisr.sauservices.ui.partner

data class Partner(
    val id: String,
    val name: String,
    val serviceIds: List<String>,
    val rating: Double,
    val completedJobs: Int,
    val experienceYears: Int,
    val distanceKm: Double,
    val startingPrice: Double,
    val about: String,
    val memberSince: String,
    val isVerified: Boolean
)
