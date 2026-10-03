package com.nisr.sauservices.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import android.os.Build
import androidx.annotation.RequiresApi
import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.auth.auth
import com.nisr.sauservices.data.model.ChatMessage
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    orderId: String,
    receiverId: String,
    receiverName: String,
    viewModel: ChatViewModel
) {
    var messageText by remember { mutableStateOf("") }
    val messages by viewModel.messages.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val currentUserId = remember { SupabaseClient.client.auth.currentUserOrNull()?.id }
    val context = LocalContext.current

    LaunchedEffect(orderId) {
        viewModel.startListening(orderId, context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(receiverName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxeTextPrimary)
                        Text("Personal Assistant", fontSize = 11.sp, color = LuxeAccentSage, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeCard)
            )
        },
        containerColor = LuxeBackground,
        bottomBar = {
            Surface(
                color = LuxeCard,
                modifier = Modifier.imePadding().fillMaxWidth(),
                border = BorderStroke(1.dp, LuxeBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("How can we help?", color = LuxeTextSecondary) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = LuxeTextPrimary,
                            unfocusedTextColor = LuxeTextPrimary
                        ),
                        maxLines = 3
                    )
                    
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(orderId, receiverId, messageText)
                                messageText = ""
                            }
                        },
                        enabled = isSending == false,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (messageText.isBlank()) LuxeBorder else LuxeAccentSage)
                    ) {
                        if (isSending == true) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                        } else {
                            Icon(Icons.AutoMirrored.Filled.Send, null, tint = if (messageText.isBlank()) LuxeTextSecondary else Color.White)
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { message ->
                val isMe = message.senderId == currentUserId
                ChatBubble(message, isMe)
            }
        }
    }
}

private fun formatTimestamp(timestamp: String): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        try {
            val zonedDateTime = ZonedDateTime.parse(timestamp)
            return zonedDateTime.format(DateTimeFormatter.ofPattern("hh:mm a"))
        } catch (e: Exception) {
            // Fallback to basic string parsing if ISO parsing fails
        }
    }
    
    // Basic fallback for older APIs or parsing errors
    // Assuming format like "2023-10-27T12:30:00Z"
    return try {
        val timePart = timestamp.substringAfter('T').substringBefore('Z')
        val parts = timePart.split(':')
        val hour = parts[0].toInt()
        val minute = parts[1]
        val amPm = if (hour >= 12) "PM" else "AM"
        val displayHour = if (hour > 12) hour - 12 else if (hour == 0) 12 else hour
        "$displayHour:$minute $amPm"
    } catch (e: Exception) {
        "12:00 PM"
    }
}

@Composable
fun ChatBubble(message: ChatMessage, isMe: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isMe) LuxeAccentSage else LuxeCard,
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = if (isMe) 24.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 24.dp
            ),
            border = if (isMe) null else BorderStroke(1.dp, LuxeBorder),
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Text(
                    text = message.content,
                    color = if (isMe) Color.White else LuxeTextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                
                // Luxe Detail: Message status and Time
                Row(
                    modifier = Modifier.padding(top = 4.dp).align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTimestamp(message.timestamp),
                        fontSize = 10.sp,
                        color = (if (isMe) Color.White else LuxeTextSecondary).copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                    if (isMe) {
                        Spacer(Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubblePreview() {
    AppTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            ChatBubble(
                message = ChatMessage(
                    content = "Hello! How can I help you today?",
                    senderId = "user1",
                    timestamp = "2023-10-27T12:30:00Z",
                    orderId = "order1",
                    receiverId = "user2"
                ),
                isMe = false
            )
            Spacer(modifier = Modifier.height(16.dp))
            ChatBubble(
                message = ChatMessage(
                    content = "I need some help with my order.",
                    senderId = "user2",
                    timestamp = "2023-10-27T12:31:00Z",
                    orderId = "order1",
                    receiverId = "user1"
                ),
                isMe = true
            )
        }
    }
}
