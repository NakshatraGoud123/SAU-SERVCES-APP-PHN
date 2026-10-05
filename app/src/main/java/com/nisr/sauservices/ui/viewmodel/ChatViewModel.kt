package com.nisr.sauservices.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nisr.sauservices.data.model.ChatMessage
import com.nisr.sauservices.data.repository.SupabaseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri

class ChatViewModel(
    private val repository: SupabaseRepository = SupabaseRepository()
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isSending = MutableStateFlow(false)
    val isSending: StateFlow<Boolean> = _isSending.asStateFlow()

    fun startListening(orderId: String, context: Context? = null) {
        viewModelScope.launch {
            repository.listenToMessages(orderId).collect { newList ->
                if (newList.size > _messages.value.size && context != null) {
                    val lastMessage = newList.maxByOrNull { it.timestamp }
                    if (lastMessage?.senderId != repository.getCurrentUserId()) {
                        playNotificationSound(context)
                    }
                }
                _messages.value = newList.sortedBy { m -> m.timestamp }
            }
        }
    }

    private fun playNotificationSound(context: Context) {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sendMessage(orderId: String, receiverId: String, content: String) {
        if (content.isBlank()) return
        val senderId = repository.getCurrentUserId() ?: return
        
        viewModelScope.launch {
            _isSending.value = true
            val message = ChatMessage(
                orderId = orderId,
                senderId = senderId,
                receiverId = receiverId,
                content = content
            )
            repository.sendMessage(message)
            _isSending.value = false
        }
    }
}
