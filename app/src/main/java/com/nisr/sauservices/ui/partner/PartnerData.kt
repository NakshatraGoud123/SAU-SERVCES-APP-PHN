package com.nisr.sauservices.ui.partner

object PartnerData {

    val partners = listOf(
        Partner(
            id = "partner_001",
            name = "Cool Care Services",
            serviceIds = listOf(
                "ac_repair",
                "ac_service",
                "ac_installation",
                "ac6" // Added to match ResidentialData.kt IDs
            ),
            rating = 4.8,
            completedJobs = 125,
            experienceYears = 6,
            distanceKm = 2.3,
            startingPrice = 499.0,
            about = "Professional AC service and repair partner providing reliable home AC services.",
            memberSince = "2024",
            isVerified = true
        ),

        Partner(
            id = "partner_002",
            name = "Sri Sai AC Solutions",
            serviceIds = listOf(
                "ac_repair",
                "ac_service",
                "ac7" // Added to match ResidentialData.kt IDs
            ),
            rating = 4.6,
            completedJobs = 89,
            experienceYears = 4,
            distanceKm = 3.1,
            startingPrice = 599.0,
            about = "Experienced AC technician specializing in repair, maintenance and servicing.",
            memberSince = "2025",
            isVerified = true
        ),

        Partner(
            id = "partner_003",
            name = "City Cooling Experts",
            serviceIds = listOf(
                "ac_repair",
                "ac_installation",
                "ac8" // Added to match ResidentialData.kt IDs
            ),
            rating = 4.5,
            completedJobs = 74,
            experienceYears = 5,
            distanceKm = 4.2,
            startingPrice = 549.0,
            about = "Local AC service provider offering fast and affordable AC solutions.",
            memberSince = "2025",
            isVerified = true
        ),

        Partner(
            id = "partner_004",
            name = "QuickFix Home Services",
            serviceIds = listOf(
                "plumbing",
                "electrical",
                "ac_repair",
                "ac9" // Added to match ResidentialData.kt IDs
            ),
            rating = 4.4,
            completedJobs = 156,
            experienceYears = 7,
            distanceKm = 5.0,
            startingPrice = 399.0,
            about = "Multi-service home maintenance partner providing professional doorstep services.",
            memberSince = "2023",
            isVerified = true
        )
    )

    fun getPartnersForService(serviceId: String): List<Partner> {
        val result = partners.filter {
            it.serviceIds.contains(serviceId)
        }

        return if (result.isNotEmpty()) {
            result
        } else {
            // Fallback for demo
            partners.filter {
                it.serviceIds.contains("ac_repair")
            }
        }
    }

    fun getPartnerById(partnerId: String): Partner? {
        return partners.find {
            it.id == partnerId
        }
    }
}
