package com.nisr.sauservices.ui.luxury

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest

@Composable
fun LuxuryProfileScreen(
    onSignOut: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val context = LocalContext.current
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val inputStream = context.contentResolver.openInputStream(it)
                val bytes = inputStream?.readBytes()
                bytes?.let { b ->
                    viewModel.uploadProfilePicture(b) { result ->
                        if (result.isFailure) {
                            android.widget.Toast.makeText(context, result.exceptionOrNull()?.message, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize().background(LuxuryBackground)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, "Close", tint = LuxuryTextPrimary)
                }
            }

            ProfileHeader(
                name = userProfile?.name ?: "User",
                email = userProfile?.email ?: "",
                phone = userProfile?.phone ?: "",
                avatarUrl = userProfile?.profilePicUrl,
                isUploading = isUploading,
                onAvatarClick = {
                    if (!isUploading) {
                        photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                }
            )

            Spacer(modifier = Modifier.height(48.dp))

            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                LuxuryMenuItem(icon = Icons.Default.ShoppingBag, label = "My Bookings")
                LuxuryMenuItem(icon = Icons.Default.LocationOn, label = "My Addresses")
                LuxuryMenuItem(icon = Icons.Default.CreditCard, label = "Payment Methods")
                LuxuryMenuItem(icon = Icons.Default.Help, label = "Help & Support")
                LuxuryMenuItem(icon = Icons.Default.Settings, label = "Settings")
            }

            Spacer(modifier = Modifier.height(48.dp))

            LuxuryButton(text = "Sign Out", onClick = onSignOut)
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LuxurySignOutConfirmation(onConfirm: () -> Unit, onCancel: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxuryBackground.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = LuxuryCard),
            border = BorderStroke(1.dp, LuxuryBorder)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LuxuryLogo(modifier = Modifier.padding(bottom = 24.dp))

                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = LuxuryGold.copy(alpha = 0.1f),
                    border = BorderStroke(1.dp, LuxuryGold)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, tint = LuxuryGold, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                CinematicHeading(text = "Sign Out")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Are you sure you want to\nsign out of SAU Solutions?",
                    textAlign = TextAlign.Center,
                    color = LuxuryTextSecondary
                )

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LuxuryBorder)
                    ) {
                        Text("Cancel", color = LuxuryTextPrimary, fontWeight = FontWeight.Bold)
                    }

                    LuxuryButton(
                        text = "Sign Out",
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    email: String,
    phone: String,
    avatarUrl: String?,
    isUploading: Boolean,
    onAvatarClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .size(100.dp)
                .clickable(onClick = onAvatarClick, enabled = !isUploading),
            shape = CircleShape,
            color = LuxuryCard,
            border = BorderStroke(1.dp, if (isUploading) LuxuryGold.copy(alpha = 0.5f) else LuxuryGold)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (!avatarUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        alpha = if (isUploading) 0.5f else 1f
                    )
                } else {
                    Text(
                        name.take(1).uppercase(),
                        color = if (isUploading) LuxuryGold.copy(alpha = 0.5f) else LuxuryGold,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = LuxuryGold,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = name, style = MaterialTheme.typography.titleLarge, color = LuxuryTextPrimary, fontWeight = FontWeight.Bold)
        if (phone.isNotEmpty()) {
            Text(text = phone, color = LuxuryTextSecondary, fontSize = 14.sp)
        }
        Text(text = email, color = LuxuryTextSecondary, fontSize = 14.sp)
    }
}

@Composable
private fun LuxuryMenuItem(icon: ImageVector, label: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LuxuryCard),
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(8.dp),
                color = LuxuryGold.copy(alpha = 0.05f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = LuxuryGold, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, color = LuxuryTextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, null, tint = LuxuryBorder)
        }
    }
}
