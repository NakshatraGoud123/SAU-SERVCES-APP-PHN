package com.nisr.sauservices.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class MechanicCategory(
    val id: String, 
    val name: String, 
    val icon: ImageVector,
    val imageUrl: String
)
data class MechanicSubcategory(
    val id: String, 
    val categoryId: String, 
    val name: String,
    val imageUrl: String = "https://images.unsplash.com/photo-1486006920555-c77dce18193b?w=600&auto=format&fit=crop&q=80"
)
data class MechanicServiceItem(
    val id: String,
    val name: String,
    val price: Double,
    val categoryId: String,
    val subcategoryId: String,
    val estimatedMinutes: Int
)

object MechanicData {
    val categories = listOf(
        MechanicCategory("c1", "Bike Services", Icons.Default.TwoWheeler, "https://images.unsplash.com/photo-1558981806-ec527fa84c39?w=600&auto=format&fit=crop&q=80"),
        MechanicCategory("c2", "Car Services", Icons.Default.DirectionsCar, "https://images.unsplash.com/photo-1486006920555-c77dce18193b?w=600&auto=format&fit=crop&q=80"),
        MechanicCategory("c3", "Auto Services", Icons.Default.Settings, "https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=600&auto=format&fit=crop&q=80"),
        MechanicCategory("c4", "EV Services", Icons.Default.ElectricCar, "https://images.unsplash.com/photo-1563720223185-11003d516935?w=600&auto=format&fit=crop&q=80"),
        MechanicCategory("c5", "Towing Services", Icons.Default.LocalShipping, "https://images.unsplash.com/photo-1580273916550-e323be2ae537?w=600&auto=format&fit=crop&q=80")
    )

    val subcategories = listOf(
        // Bike Services
        MechanicSubcategory("s1", "c1", "General Service & Oil Change", "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=600&auto=format&fit=crop&q=80"),
        MechanicSubcategory("s2", "c1", "Brake Repair & Tune-up", "https://images.unsplash.com/photo-1591117207234-76783be2b083?w=600&auto=format&fit=crop&q=80"),
        MechanicSubcategory("s3", "c1", "Chain & Sprocket Replacement", "https://images.unsplash.com/photo-1558981806-ec527fa84c39?w=600&auto=format&fit=crop&q=80"),
        MechanicSubcategory("s4", "c1", "Engine Diagnostics", "https://images.unsplash.com/photo-1517524208157-a71d7441d5a9?w=600&auto=format&fit=crop&q=80"),
        
        // Car Services
        MechanicSubcategory("s5", "c2", "Periodic Maintenance Service", "https://images.unsplash.com/photo-1486006920555-c77dce18193b?w=600&auto=format&fit=crop&q=80"),
        MechanicSubcategory("s6", "c2", "AC Repair & Gas Refill", "https://images.unsplash.com/photo-1580273916550-e323be2ae537?w=600&auto=format&fit=crop&q=80"),
        MechanicSubcategory("s7", "c2", "Battery Jumpstart & Replacement", "https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=600&auto=format&fit=crop&q=80"),
        MechanicSubcategory("s8", "c2", "Dent & Paint Services", "https://images.unsplash.com/photo-1503376780353-7e6692767b70?w=600&auto=format&fit=crop&q=80"),

        // Auto Services
        MechanicSubcategory("s9", "c3", "Auto Rickshaw General Checkup", "https://images.unsplash.com/photo-1619642751034-765dfdf7c58e?w=600&auto=format&fit=crop&q=80"),
        MechanicSubcategory("s10", "c3", "Clutch & Gearbox Repair", "https://images.unsplash.com/photo-1486006920555-c77dce18193b?w=600&auto=format&fit=crop&q=80"),

        // EV Services
        MechanicSubcategory("s11", "c4", "EV Battery Health Check", "https://images.unsplash.com/photo-1563720223185-11003d516935?w=600&auto=format&fit=crop&q=80"),
        MechanicSubcategory("s12", "c4", "Motor Controller Diagnostics", "https://images.unsplash.com/photo-1563720223185-11003d516935?w=600&auto=format&fit=crop&q=80"),

        // Towing Services
        MechanicSubcategory("s13", "c5", "Flatbed Towing", "https://images.unsplash.com/photo-1580273916550-e323be2ae537?w=600&auto=format&fit=crop&q=80"),
        MechanicSubcategory("s14", "c5", "Roadside Assistance", "https://images.unsplash.com/photo-1580273916550-e323be2ae537?w=600&auto=format&fit=crop&q=80")
    )

    val services = listOf(
        MechanicServiceItem("ms1", "Full Bike Service", 499.0, "c1", "s1", 45),
        MechanicServiceItem("ms2", "Synthetic Oil Change", 299.0, "c1", "s1", 20),
        MechanicServiceItem("ms3", "Standard Car Periodic Service", 2499.0, "c2", "s5", 120),
        MechanicServiceItem("ms4", "Car AC Gas Refill", 1199.0, "c2", "s6", 45)
    )
}
