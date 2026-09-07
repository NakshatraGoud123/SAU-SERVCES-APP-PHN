package com.nisr.sauservices.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    @SerialName("id") val id: String = "",
    @SerialName("order_id") val orderId: String,
    @SerialName("sender_id") val senderId: String,
    @SerialName("receiver_id") val receiverId: String,
    @SerialName("content") val content: String,
    @SerialName("created_at") val timestamp: String = ""
)
