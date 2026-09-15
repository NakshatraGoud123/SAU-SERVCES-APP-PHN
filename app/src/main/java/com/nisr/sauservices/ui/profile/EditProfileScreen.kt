package com.nisr.sauservices.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel
import io.github.jan.supabase.auth.auth
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController, 
    viewModel: ProfileViewModel = viewModel()
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    // Check Auth for fallbacks
    val client = com.nisr.sauservices.data.api.SupabaseClient.client
    val authUser = client.auth.currentUserOrNull()
    
    LaunchedEffect(userProfile) {
        userProfile?.let {
            name = it.name.ifEmpty { "User" }
            phone = it.phone
            email = it.email.ifEmpty { authUser?.email ?: "" }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val byteArray = inputStream?.readBytes()
                byteArray?.let { bytes ->
                    viewModel.uploadProfilePicture(bytes) { result ->
                        if (result.isSuccess) {
                            Toast.makeText(context, "Profile picture updated!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Upload failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to read image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        containerColor = LuxeBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Luxe Profile Picture Section
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(130.dp)
                    .clickable { 
                        photoPickerLauncher.launch("image/*")
                    }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .shadow(4.dp, CircleShape),
                    color = LuxeHighlightChampagne,
                    border = BorderStroke(2.dp, LuxeCard)
                ) {
                    val picUrl = userProfile?.profilePicUrl
                    if (!picUrl.isNullOrEmpty()) {
                        Box(contentAlignment = Alignment.Center) {
                            val imageUrl = if (picUrl.contains("?")) "$picUrl&t=${System.currentTimeMillis()}" else "$picUrl?t=${System.currentTimeMillis()}"
                            
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(30.dp), color = LuxeAccentSage, strokeWidth = 2.dp)
                            }
                        }
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            val initials = (if (name.isNotEmpty()) name else "U").take(1).uppercase()
                            Text(
                                initials,
                                color = LuxeTextPrimary,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }
                }
                
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = LuxeAccentSage,
                    border = BorderStroke(1.dp, LuxeCard)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change Picture",
                        modifier = Modifier.padding(10.dp).size(20.dp),
                        tint = Color.White
                    )
                }
            }

            Text(
                "TAP TO CHANGE AVATAR",
                fontSize = 11.sp,
                color = LuxeTextSecondary,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 20.dp, bottom = 48.dp)
            )

            // Luxury Form Fields
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                LuxuryTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "FULL NAME",
                    leadingIcon = Icons.Default.Person
                )

                LuxuryTextField(
                    value = email,
                    onValueChange = { },
                    label = "EMAIL ADDRESS",
                    leadingIcon = Icons.Default.Email,
                    enabled = false
                )

                LuxuryTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "PHONE NUMBER",
                    leadingIcon = Icons.Default.Phone,
                    keyboardType = KeyboardType.Phone
                )
            }

            Spacer(Modifier.height(56.dp))

            LuxuryButton(
                text = "SAVE CHANGES",
                onClick = {
                    if (name.isNotEmpty()) {
                        viewModel.updateProfile(name, phone) { result ->
                            if (result.isSuccess) {
                                Toast.makeText(context, "Profile Updated Successfully!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(context, "Update failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please enter a valid name", Toast.LENGTH_SHORT).show()
                    }
                },
                isLoading = isLoading
            )
        }
    }
}
