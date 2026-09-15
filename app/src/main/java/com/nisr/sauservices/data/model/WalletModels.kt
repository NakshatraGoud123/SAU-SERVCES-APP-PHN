package com.nisr.sauservices.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String,
    @SerialName("amount") val amount: Double,
    @SerialName("type") val type: String, // 'credit', 'debit'
    @SerialName("description") val description: String,
    @SerialName("created_at") val createdAt: String? = null
)
