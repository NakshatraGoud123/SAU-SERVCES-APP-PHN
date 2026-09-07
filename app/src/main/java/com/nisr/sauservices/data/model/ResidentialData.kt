package com.nisr.sauservices.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.nisr.sauservices.R

object ResidentialData {
    val categories = listOf(
        ResidentialCategory("electrician", "Electrician", Icons.Default.ElectricBolt, R.drawable.electrician), // Standard: srv_electrician
        ResidentialCategory("plumber", "Plumber", Icons.Default.WaterDrop, R.drawable.plumber), // Standard: srv_plumber
        ResidentialCategory("carpenter", "Carpenter", Icons.Default.Handyman, R.drawable.property_lifestyle), // Standard: srv_carpenter
        ResidentialCategory("painter", "Painter", Icons.Default.FormatPaint, R.drawable.cleaning), // Standard: srv_painter
        ResidentialCategory("ac_repair", "AC Repair", Icons.Default.Air, R.drawable.ac_repair), // Standard: srv_ac_repair
        ResidentialCategory("laundry", "Laundry", Icons.Default.LocalLaundryService, R.drawable.cleaning), // Standard: srv_laundry
        ResidentialCategory("appliance", "Appliance Repair", Icons.Default.Kitchen, R.drawable.tech_services), // Standard: srv_appliance
        ResidentialCategory("pest_control", "Pest Control", Icons.Default.BugReport, R.drawable.cleaning), // Standard: srv_pest_control
        ResidentialCategory("home_cleaning", "Home Cleaning", Icons.Default.CleaningServices, R.drawable.cleaning) // Standard: srv_cleaning
    )

    val subcategories = listOf(
        // Electrician
        ResidentialSubcategory("elec_repairs", "electrician", "Repairs"),
        ResidentialSubcategory("elec_install", "electrician", "Installation"),
        ResidentialSubcategory("elec_inspect", "electrician", "Inspection"),
        // Plumber
        ResidentialSubcategory("plum_repairs", "plumber", "Repairs"),
        ResidentialSubcategory("plum_install", "plumber", "Installation"),
        // Carpenter
        ResidentialSubcategory("carp_repairs", "carpenter", "Furniture Repair"),
        ResidentialSubcategory("carp_assembly", "carpenter", "Assembly"),
        // Painter
        ResidentialSubcategory("paint_wall", "painter", "Wall Painting"),
        ResidentialSubcategory("paint_touchup", "painter", "Touchups"),
        // AC Repair
        ResidentialSubcategory("ac_service", "ac_repair", "Servicing"),
        ResidentialSubcategory("ac_repair_sub", "ac_repair", "Repair"),
        // Laundry
        ResidentialSubcategory("laundry_wash", "laundry", "Laundry"),
        ResidentialSubcategory("laundry_dry", "laundry", "Dry Clean"),
        // Appliance
        ResidentialSubcategory("app_kitchen", "appliance", "Kitchen"),
        ResidentialSubcategory("app_home", "appliance", "Home"),
        // Pest Control
        ResidentialSubcategory("pest_basic", "pest_control", "Basic"),
        ResidentialSubcategory("pest_advanced", "pest_control", "Advanced"),
        // Home Cleaning
        ResidentialSubcategory("clean_room", "home_cleaning", "Room Cleaning"),
        ResidentialSubcategory("clean_full", "home_cleaning", "Full Home")
    )

    val services = listOf(
        // Electrician - Repairs
        ResidentialServiceItem("e1", "Switch Repair", 99.0, "electrician", "elec_repairs", 30),
        ResidentialServiceItem("e2", "Fan Repair", 149.0, "electrician", "elec_repairs", 45),
        ResidentialServiceItem("e3", "Tube Light Repair", 99.0, "electrician", "elec_repairs", 30),
        ResidentialServiceItem("e4", "Socket Repair", 129.0, "electrician", "elec_repairs", 30),
        ResidentialServiceItem("e5", "Door Bell Fix", 149.0, "electrician", "elec_repairs", 30),
        ResidentialServiceItem("e6", "Wiring Issue Fix", 199.0, "electrician", "elec_repairs", 60),
        // Electrician - Installation
        ResidentialServiceItem("e7", "Fan Installation", 249.0, "electrician", "elec_install", 45),
        ResidentialServiceItem("e8", "Light Installation", 149.0, "electrician", "elec_install", 30),
        ResidentialServiceItem("e9", "Switch Board Install", 299.0, "electrician", "elec_install", 60),
        ResidentialServiceItem("e10", "Decorative Lights Setup", 349.0, "electrician", "elec_install", 90),
        ResidentialServiceItem("e11", "Exhaust Fan Install", 299.0, "electrician", "elec_install", 45),
        // Electrician - Inspection
        ResidentialServiceItem("e12", "Full House Check", 399.0, "electrician", "elec_inspect", 120),
        ResidentialServiceItem("e13", "Safety Inspection", 299.0, "electrician", "elec_inspect", 60),
        ResidentialServiceItem("e14", "Power Load Check", 249.0, "electrician", "elec_inspect", 45),
        ResidentialServiceItem("e15", "Earthing Check", 199.0, "electrician", "elec_inspect", 45),
        ResidentialServiceItem("e16", "Fuse Inspection", 149.0, "electrician", "elec_inspect", 30),

        // Plumber - Repairs
        ResidentialServiceItem("p1", "Tap Repair", 99.0, "plumber", "plum_repairs", 30),
        ResidentialServiceItem("p2", "Pipe Leak Fix", 149.0, "plumber", "plum_repairs", 45),
        ResidentialServiceItem("p3", "Flush Repair", 199.0, "plumber", "plum_repairs", 45),
        ResidentialServiceItem("p4", "Drain Block Fix", 199.0, "plumber", "plum_repairs", 60),
        ResidentialServiceItem("p5", "Shower Leak Fix", 149.0, "plumber", "plum_repairs", 30),
        // Plumber - Installation
        ResidentialServiceItem("p6", "Basin Install", 299.0, "plumber", "plum_install", 90),
        ResidentialServiceItem("p7", "Sink Install", 349.0, "plumber", "plum_install", 90),
        ResidentialServiceItem("p8", "Shower Install", 249.0, "plumber", "plum_install", 45),
        ResidentialServiceItem("p9", "Water Motor Install", 499.0, "plumber", "plum_install", 120),
        ResidentialServiceItem("p10", "Toilet Install", 399.0, "plumber", "plum_install", 180),

        // Carpenter - Repairs
        ResidentialServiceItem("c1", "Bed Repair", 399.0, "carpenter", "carp_repairs", 90),
        ResidentialServiceItem("c2", "Table Repair", 249.0, "carpenter", "carp_repairs", 60),
        ResidentialServiceItem("c3", "Chair Repair", 149.0, "carpenter", "carp_repairs", 45),
        ResidentialServiceItem("c4", "Sofa Repair", 499.0, "carpenter", "carp_repairs", 120),
        ResidentialServiceItem("c5", "Door Repair", 299.0, "carpenter", "carp_repairs", 60),
        // Carpenter - Assembly
        ResidentialServiceItem("c6", "Wardrobe Assembly", 499.0, "carpenter", "carp_assembly", 180),
        ResidentialServiceItem("c7", "Modular Kitchen Setup", 999.0, "carpenter", "carp_assembly", 360),
        ResidentialServiceItem("c8", "TV Unit Install", 349.0, "carpenter", "carp_assembly", 90),
        ResidentialServiceItem("c9", "Shelf Installation", 199.0, "carpenter", "carp_assembly", 45),
        ResidentialServiceItem("c10", "Curtain Rod Install", 149.0, "carpenter", "carp_assembly", 30),

        // Painter - Wall Painting
        ResidentialServiceItem("pa1", "Single Wall Paint", 499.0, "painter", "paint_wall", 120),
        ResidentialServiceItem("pa2", "Room Painting", 1500.0, "painter", "paint_wall", 360),
        ResidentialServiceItem("pa3", "Full Home Painting", 6000.0, "painter", "paint_wall", 1440),
        ResidentialServiceItem("pa4", "Texture Paint", 999.0, "painter", "paint_wall", 240),
        ResidentialServiceItem("pa5", "Waterproof Coating", 1299.0, "painter", "paint_wall", 300),
        // Painter - Touchups
        ResidentialServiceItem("pa6", "Crack Filling", 299.0, "painter", "paint_touchup", 60),
        ResidentialServiceItem("pa7", "Spot Painting", 199.0, "painter", "paint_touchup", 45),
        ResidentialServiceItem("pa8", "Ceiling Paint", 399.0, "painter", "paint_touchup", 90),
        ResidentialServiceItem("pa9", "Stain Removal Paint", 349.0, "painter", "paint_touchup", 60),

        // AC Repair - Servicing
        ResidentialServiceItem("ac1", "General Service", 399.0, "ac_repair", "ac_service", 60),
        ResidentialServiceItem("ac2", "Deep Service", 699.0, "ac_repair", "ac_service", 90),
        ResidentialServiceItem("ac3", "Jet Cleaning", 999.0, "ac_repair", "ac_service", 120),
        ResidentialServiceItem("ac4", "Filter Cleaning", 299.0, "ac_repair", "ac_service", 30),
        ResidentialServiceItem("ac5", "Gas Check", 199.0, "ac_repair", "ac_service", 30),
        // AC Repair - Repair
        ResidentialServiceItem("ac6", "Gas Refill", 1499.0, "ac_repair", "ac_repair_sub", 120),
        ResidentialServiceItem("ac7", "Cooling Issue Fix", 999.0, "ac_repair", "ac_repair_sub", 90),
        ResidentialServiceItem("ac8", "Water Leakage Fix", 799.0, "ac_repair", "ac_repair_sub", 60),
        ResidentialServiceItem("ac9", "Noise Issue Fix", 699.0, "ac_repair", "ac_repair_sub", 60),
        ResidentialServiceItem("ac10", "PCB Repair", 1299.0, "ac_repair", "ac_repair_sub", 180),

        // Laundry - Laundry
        ResidentialServiceItem("l1", "Wash & Fold", 99.0, "laundry", "laundry_wash", 1440),
        ResidentialServiceItem("l2", "Wash & Iron", 149.0, "laundry", "laundry_wash", 1440),
        ResidentialServiceItem("l3", "Express Wash", 199.0, "laundry", "laundry_wash", 480),
        ResidentialServiceItem("l4", "Premium Wash", 249.0, "laundry", "laundry_wash", 1440),
        ResidentialServiceItem("l5", "Blanket Wash", 299.0, "laundry", "laundry_wash", 1440),
        // Laundry - Dry Clean
        ResidentialServiceItem("l6", "Shirt", 79.0, "laundry", "laundry_dry", 2880),
        ResidentialServiceItem("l7", "Suit", 199.0, "laundry", "laundry_dry", 2880),
        ResidentialServiceItem("l8", "Saree", 149.0, "laundry", "laundry_dry", 2880),
        ResidentialServiceItem("l9", "Jacket", 199.0, "laundry", "laundry_dry", 2880),
        ResidentialServiceItem("l10", "Curtain", 299.0, "laundry", "laundry_dry", 2880),

        // Appliance Repair - Kitchen
        ResidentialServiceItem("ap1", "Refrigerator", 399.0, "appliance", "app_kitchen", 60),
        ResidentialServiceItem("ap2", "Microwave", 299.0, "appliance", "app_kitchen", 45),
        ResidentialServiceItem("ap3", "Chimney", 349.0, "appliance", "app_kitchen", 60),
        ResidentialServiceItem("ap4", "Induction Stove", 249.0, "appliance", "app_kitchen", 45),
        ResidentialServiceItem("ap5", "RO Repair", 299.0, "appliance", "app_kitchen", 60),
        // Appliance Repair - Home
        ResidentialServiceItem("ap6", "Washing Machine", 499.0, "appliance", "app_home", 90),
        ResidentialServiceItem("ap7", "Geyser", 299.0, "appliance", "app_home", 45),
        ResidentialServiceItem("ap8", "TV", 399.0, "appliance", "app_home", 60),
        ResidentialServiceItem("ap9", "Water Purifier", 299.0, "appliance", "app_home", 45),
        ResidentialServiceItem("ap10", "Air Cooler", 249.0, "appliance", "app_home", 45),

        // Pest Control - Basic
        ResidentialServiceItem("pc1", "Cockroach Control", 799.0, "pest_control", "pest_basic", 60),
        ResidentialServiceItem("pc2", "Mosquito Control", 699.0, "pest_control", "pest_basic", 60),
        ResidentialServiceItem("pc3", "Ant Control", 599.0, "pest_control", "pest_basic", 45),
        ResidentialServiceItem("pc4", "Fly Control", 499.0, "pest_control", "pest_basic", 45),
        ResidentialServiceItem("pc5", "Lizard Control", 399.0, "pest_control", "pest_basic", 45),
        // Pest Control - Advanced
        ResidentialServiceItem("pc6", "Termite", 1999.0, "pest_control", "pest_advanced", 180),
        ResidentialServiceItem("pc7", "Bed Bugs", 1499.0, "pest_control", "pest_advanced", 120),
        ResidentialServiceItem("pc8", "Rodent Control", 999.0, "pest_control", "pest_advanced", 90),
        ResidentialServiceItem("pc9", "Wood Borer", 1199.0, "pest_control", "pest_advanced", 120),
        ResidentialServiceItem("pc10", "Full Home Pest Control", 2499.0, "pest_control", "pest_advanced", 240),

        // Home Cleaning - Room Cleaning
        ResidentialServiceItem("hc1", "Bedroom", 499.0, "home_cleaning", "clean_room", 60),
        ResidentialServiceItem("hc2", "Bathroom", 399.0, "home_cleaning", "clean_room", 45),
        ResidentialServiceItem("hc3", "Kitchen", 599.0, "home_cleaning", "clean_room", 90),
        ResidentialServiceItem("hc4", "Balcony", 299.0, "home_cleaning", "clean_room", 30),
        ResidentialServiceItem("hc5", "Sofa Cleaning", 699.0, "home_cleaning", "clean_room", 90),
        // Home Cleaning - Full Home
        ResidentialServiceItem("hc6", "1BHK", 1499.0, "home_cleaning", "clean_full", 240),
        ResidentialServiceItem("hc7", "2BHK", 1999.0, "home_cleaning", "clean_full", 360),
        ResidentialServiceItem("hc8", "3BHK", 2499.0, "home_cleaning", "clean_full", 480),
        ResidentialServiceItem("hc9", "4BHK", 2999.0, "home_cleaning", "clean_full", 600),
        ResidentialServiceItem("hc10", "Villa Cleaning", 4999.0, "home_cleaning", "clean_full", 720)
    )
}
