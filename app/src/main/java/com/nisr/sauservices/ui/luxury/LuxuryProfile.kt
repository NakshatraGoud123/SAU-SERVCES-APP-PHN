package com.nisr.sauservices.ui.luxury

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import androidx.compose.ui.text.font.FontFamily

// ============================================================
// LUXE BRAND COLORS (Local for precision)
// ============================================================
private val LuxeBackground = Color(0xFFFDFBFA)
private val LuxeCard = Color(0xFFFFFFFF)
private val LuxeTextPrimary = Color(0xFF423F3D)
private val LuxeTextSecondary = Color(0xFF8D7F77)
private val LuxeAccentSage = Color(0xFF96A68F)
private val LuxeHighlightChampagne = Color(0xFFF5E6D3)
private val LuxeBorder = Color(0xFFEFE9E4)
private val LuxeGold = Color(0xFFE8C66A)


@Composable
fun LuxuryProfileScreen(
    navController: NavController,
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
                    // Professional Check: Limit to 5MB (5 * 1024 * 1024 bytes)
                    if (b.size > 5 * 1024 * 1024) {
                        android.widget.Toast.makeText(context, "Image is too large. Please select a photo under 5MB.", android.widget.Toast.LENGTH_LONG).show()
                        return@let
                    }
                    
                    viewModel.uploadProfilePicture(b) { result ->
                        if (result.isFailure) {
                            val error = result.exceptionOrNull()?.message ?: ""
                            val friendlyMsg = if (error.contains("size", true)) 
                                "This photo is too high-resolution. Please try a smaller one." 
                                else error
                            android.widget.Toast.makeText(context, friendlyMsg, android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    )

    Box(modifier = Modifier.fillMaxSize().background(LuxeBackground)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, "Close", tint = LuxeTextPrimary)
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
                LuxuryMenuItem(icon = Icons.Default.AccountBalanceWallet, label = "My Wallet") {
                    navController.navigate(com.nisr.sauservices.ui.Screen.Wallet)
                }
                LuxuryMenuItem(icon = Icons.Default.ShoppingBag, label = "My Bookings") {
                    navController.navigate(com.nisr.sauservices.ui.Screen.MyBookings)
                }
                LuxuryMenuItem(icon = Icons.Default.LocationOn, label = "My Addresses") {
                    navController.navigate(com.nisr.sauservices.ui.Screen.ShippingAddress)
                }
                LuxuryMenuItem(icon = Icons.Default.CreditCard, label = "Payment Methods") {
                    navController.navigate(com.nisr.sauservices.ui.Screen.ResidentialPayment("multi", "multi"))
                }
                LuxuryMenuItem(icon = Icons.Default.Help, label = "Help & Support") {
                    navController.navigate(com.nisr.sauservices.ui.Screen.ContactUs)
                }
                LuxuryMenuItem(icon = Icons.Default.Settings, label = "Settings") {
                    navController.navigate(com.nisr.sauservices.ui.Screen.Settings)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            LuxuryButton(text = "Sign Out", onClick = onSignOut, modifier = Modifier.height(50.dp))
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LuxurySignOutConfirmation(onConfirm: () -> Unit, onCancel: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LuxeBackground.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = LuxeCard),
            border = BorderStroke(1.dp, LuxeBorder),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LuxuryLogo(modifier = Modifier.padding(bottom = 24.dp))

                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = LuxeHighlightChampagne,
                    border = BorderStroke(1.dp, LuxeBorder)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, tint = LuxeTextPrimary, modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sign Out",
                    style = MaterialTheme.typography.headlineSmall,
                    color = LuxeTextPrimary,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Are you sure you want to\nsign out of SAU Solutions?",
                    textAlign = TextAlign.Center,
                    color = LuxeTextSecondary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, LuxeBorder)
                    ) {
                        Text("Cancel", color = LuxeTextPrimary, fontWeight = FontWeight.Bold)
                    }

                    LuxuryButton(
                        text = "Sign Out",
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f).height(50.dp)
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
                .size(110.dp)
                .clickable(onClick = onAvatarClick, enabled = !isUploading)
                .shadow(4.dp, CircleShape),
            shape = CircleShape,
            color = LuxeHighlightChampagne,
            border = BorderStroke(2.dp, LuxeCard)
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (!avatarUrl.isNullOrEmpty()) {
                    // Force refresh with a timestamp
                    val imageUrl = if (avatarUrl.contains("?")) "$avatarUrl&t=${System.currentTimeMillis()}" else "$avatarUrl?t=${System.currentTimeMillis()}"
                    
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        alpha = if (isUploading) 0.5f else 1f
                    )
                } else {
                    Text(
                        name.take(1).uppercase(),
                        color = LuxeTextPrimary,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif
                    )
                }

                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = LuxeAccentSage,
                        strokeWidth = 3.dp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = name, 
            style = MaterialTheme.typography.headlineSmall, 
            color = LuxeTextPrimary, 
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Serif
        )
        if (phone.isNotEmpty()) {
            Text(text = phone, color = LuxeTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Text(text = email, color = LuxeTextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LuxuryMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(12.dp),
                color = LuxeHighlightChampagne.copy(alpha = 0.5f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = LuxeTextPrimary, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label, 
                color = LuxeTextPrimary, 
                fontWeight = FontWeight.Bold, 
                fontSize = 15.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Default.ChevronRight, null, tint = LuxeTextSecondary, modifier = Modifier.size(20.dp))
        }
    }
}
