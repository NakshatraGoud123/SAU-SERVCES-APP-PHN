package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class UserProfile(
    val id: String = "",
    @SerialName("name") val name: String = "",
    val email: String = "",
    @SerialName("phone_number") val phone: String = "",
    @SerialName("avatar_url") val profilePicUrl: String? = null,
    val role: String = "customer"
)

@Serializable
data class Address(
    val id: String = "",
    @SerialName("name") val fullName: String = "",
    val phone: String = "",
    @SerialName("house_no") val houseNo: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val landmark: String = "",
    @SerialName("is_default") val isDefault: Boolean = false,
    @SerialName("user_id") val userId: String = ""
)

@Serializable
data class NotificationPreferences(
    @SerialName("order_updates") val orderUpdates: Boolean = true,
    val promotions: Boolean = true,
    @SerialName("service_alerts") val serviceAlerts: Boolean = true,
    @SerialName("app_updates") val appUpdates: Boolean = true
)
