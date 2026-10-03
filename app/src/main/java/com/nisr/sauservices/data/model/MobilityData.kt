package com.nisr.sauservices.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
    val serviceTypes = listOf(
        MobilityServiceType("m1", "Bike Ride", Icons.Default.TwoWheeler, 30.0, 10.0, "Fast & affordable city commute"),
        MobilityServiceType("m2", "Auto Ride", Icons.Default.ElectricRickshaw, 50.0, 14.0, "Comfortable 3-wheeler auto ride"),
        MobilityServiceType("m3", "Cab Ride", Icons.Default.DirectionsCar, 100.0, 20.0, "Spacious AC hatchback & sedan"),
        MobilityServiceType("m4", "Parcel Delivery", Icons.Default.Inventory2, 40.0, 12.0, "Send packages across town instantly"),
        MobilityServiceType("m5", "Airport Pickup", Icons.Default.FlightTakeoff, 350.0, 22.0, "Reliable airport transfers"),
        MobilityServiceType("m6", "Rental Ride", Icons.Default.Key, 200.0, 15.0, "Hourly rental cabs & bikes")
    )

    val cabCategories = serviceTypes

    val parcelCategories = listOf(
        MobilityParcelCategory("p1", "Documents & Keys", Icons.Default.Description),
        MobilityParcelCategory("p2", "Food & Grocery", Icons.Default.Fastfood),
        MobilityParcelCategory("p3", "Electronics", Icons.Default.Devices),
        MobilityParcelCategory("p4", "Clothing & Gifts", Icons.Default.CardGiftcard)
    )
}
