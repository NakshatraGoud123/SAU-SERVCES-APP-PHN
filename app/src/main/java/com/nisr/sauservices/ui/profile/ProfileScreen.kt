package com.nisr.sauservices.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    val userProfile by viewModel.userProfile.collectAsState()
    val notificationPrefs by viewModel.notificationPrefs.collectAsState()
    val addresses by viewModel.addresses.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val sessionManager = remember { com.nisr.sauservices.data.local.SessionManager(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, null, tint = LuxuryGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        containerColor = LuxuryBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            MinimalProfileHeader(
                name = userProfile?.name ?: "User",
                phone = userProfile?.phone ?: "",
                profilePicUrl = userProfile?.profilePicUrl,
                onEdit = { navController.navigate(Screen.EditProfile) }
            )

            SavedAddressesSection(
                navController = navController,
                addresses = addresses
            )

            NotificationPreferencesSection(notificationPrefs, viewModel)

            AccountSettingsSection(navController, onLogout = { showLogoutDialog = true })
            
            Spacer(Modifier.height(16.dp))
        }
    }
    
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", color = LuxuryTextPrimary) },
            text = { Text("Are you sure you want to logout?", color = LuxuryTextSecondary) },
            containerColor = LuxuryCard,
            confirmButton = {
                TextButton(onClick = {
                    viewModel.logout()
                    sessionManager.logout()
                    showLogoutDialog = false
                    navController.navigate(Screen.Login()) {
                        popUpTo(0)
                    }
                }) {
                    Text("Logout", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = LuxuryTextPrimary)
                }
            }
        )
    }
}

@Composable
fun MinimalProfileHeader(name: String, phone: String, profilePicUrl: String? = null, onEdit: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(LuxuryGold.copy(alpha = 0.1f))
                    .border(1.dp, LuxuryGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!profilePicUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = profilePicUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        error = androidx.compose.ui.graphics.painter.ColorPainter(LuxuryGold.copy(alpha = 0.1f))
                    )
                } else {
                    Text(
                        name.take(1).uppercase(),
                        color = LuxuryGold,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            OutlinedButton(
                onClick = onEdit,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, LuxuryBorder),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxuryGold)
            ) {
                Text("Edit Profile", color = LuxuryGold, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        
        Spacer(Modifier.height(24.dp))
        
        Text(
            text = "Hello,",
            fontSize = 17.sp,
            color = LuxuryTextSecondary,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = name,
            fontSize = 32.sp,
            color = LuxuryTextPrimary,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 36.sp
        )
        Text(
            text = phone,
            fontSize = 15.sp,
            color = LuxuryTextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun SavedAddressesSection(navController: NavController, addresses: List<com.nisr.sauservices.data.model.Address>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "SAVED ADDRESSES", 
                fontSize = 13.sp, 
                color = LuxuryTextSecondary, 
                fontWeight = FontWeight.ExtraBold, 
                letterSpacing = 1.sp
            )
            TextButton(onClick = { 
                navController.navigate(Screen.MapPicker)
            }) {
                Text("Add New", color = LuxuryGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        
        Spacer(Modifier.height(12.dp))
        
        if (addresses.isEmpty()) {
            Text(
                "No addresses saved yet",
                color = LuxuryTextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            addresses.take(2).forEach { address ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(Screen.Settings) }
                        .padding(vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn, 
                            null, 
                            tint = LuxuryTextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(address.city.ifEmpty { "Saved Location" }, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = LuxuryTextPrimary)
                            Text(
                                "${address.houseNo} ${address.street}, ${address.city}", 
                                fontSize = 14.sp, 
                                color = LuxuryTextSecondary,
                                maxLines = 1
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight, 
                            null, 
                            tint = LuxuryBorder,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                HorizontalDivider(color = LuxuryBorder, thickness = 1.dp)
            }
        }
    }
}

@Composable
fun AccountSettingsSection(navController: NavController, onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "ACCOUNT SETTINGS", 
            fontSize = 13.sp, 
            color = LuxuryTextSecondary, 
            fontWeight = FontWeight.ExtraBold, 
            letterSpacing = 1.sp
        )
        
        Spacer(Modifier.height(16.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MinimalMenuItem(icon = Icons.Outlined.FavoriteBorder, label = "Favourite stores") { 
                navController.navigate(Screen.Categories) 
            }
            MinimalMenuItem(icon = Icons.Outlined.Notifications, label = "Notifications") { 
                navController.navigate(Screen.Notifications) 
            }
            MinimalMenuItem(icon = Icons.Outlined.Settings, label = "Settings") { 
                navController.navigate(Screen.Settings) 
            }
            MinimalMenuItem(icon = Icons.AutoMirrored.Outlined.HelpOutline, label = "Help & Support") { 
                navController.navigate(Screen.ContactUs)
            }
            MinimalMenuItem(icon = Icons.Outlined.QuestionAnswer, label = "FAQs") { 
                navController.navigate(Screen.FAQ)
            }
            
            Spacer(Modifier.height(24.dp))
            
            TextButton(
                onClick = onLogout,
                modifier = Modifier.padding(start = 0.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Logout, 
                        null, 
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Log out", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun MinimalMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = LuxuryTextPrimary, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = LuxuryTextPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.ChevronRight, 
            null, 
            tint = LuxuryBorder,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun NotificationPreferencesSection(
    prefs: com.nisr.sauservices.data.model.NotificationPreferences,
    viewModel: ProfileViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "NOTIFICATION PREFERENCES", 
            fontSize = 13.sp, 
            color = LuxuryTextSecondary, 
            fontWeight = FontWeight.ExtraBold, 
            letterSpacing = 1.sp
        )
        
        Spacer(Modifier.height(16.dp))
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NotificationToggleItem(
                title = "Order Updates",
                description = "Status of your active bookings",
                checked = prefs.orderUpdates,
                onCheckedChange = { viewModel.updateNotificationPref("order_updates", it) }
            )
            NotificationToggleItem(
                title = "Promotions",
                description = "Latest deals and exclusive offers",
                checked = prefs.promotions,
                onCheckedChange = { viewModel.updateNotificationPref("promotions", it) }
            )
        }
    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = LuxuryCard,
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title, 
                    fontWeight = FontWeight.ExtraBold, 
                    fontSize = 16.sp, 
                    color = LuxuryTextPrimary
                )
                Text(
                    text = description, 
                    fontSize = 12.sp, 
                    color = LuxuryTextSecondary
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = LuxuryBackground,
                    checkedTrackColor = LuxuryGold,
                    uncheckedThumbColor = LuxuryTextSecondary,
                    uncheckedTrackColor = LuxuryCard,
                    uncheckedBorderColor = LuxuryBorder
                )
            )
        }
    }
}
