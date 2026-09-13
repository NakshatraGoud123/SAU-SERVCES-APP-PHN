package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class User(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("phone") val phone: String = "",
    @SerialName("user_type") val role: String = "customer", // shopkeeper, service_worker, delivery, customer
    @SerialName("avatar_url") val avatarUrl: String? = null,
    @SerialName("status") val status: String = "active",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("extra_fields") val extraFields: Map<String, String> = emptyMap()
) {
    val displayName: String get() = name.ifEmpty { "Anonymous User" }
    val displayEmail: String get() = email.ifEmpty { "No email provided" }
    val displayPhone: String get() = phone.ifEmpty { "No phone" }
}
