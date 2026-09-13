package com.nisr.sauservices.ui.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.api.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontFamily
import com.nisr.sauservices.ui.components.LuxuryButton

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(navController: NavController) {
    val auth = SupabaseClient.client.auth
    val user = auth.currentUserOrNull()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val isEmailUser = user?.identities?.any { it.provider == "email" } ?: false

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Password", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif) },
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (!isEmailUser) {
                Surface(
                    color = LuxeHighlightChampagne.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, LuxeBorder)
                ) {
                    Text(
                        "Password change is not available for social accounts.",
                        modifier = Modifier.padding(16.dp),
                        color = LuxeTextSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            OutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = { Text("Current Password", color = LuxeTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Icon(if (currentPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = LuxeTextSecondary)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuxeAccentSage,
                    unfocusedBorderColor = LuxeBorder,
                    focusedLabelColor = LuxeAccentSage
                )
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("New Password", color = LuxeTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = LuxeTextSecondary)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuxeAccentSage,
                    unfocusedBorderColor = LuxeBorder,
                    focusedLabelColor = LuxeAccentSage
                )
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm New Password", color = LuxeTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null, tint = LuxeTextSecondary)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LuxeAccentSage,
                    unfocusedBorderColor = LuxeBorder,
                    focusedLabelColor = LuxeAccentSage
                )
            )

            Spacer(Modifier.height(16.dp))

            LuxuryButton(
                text = "UPDATE PASSWORD",
                onClick = {
                    if (!isEmailUser) {
                        Toast.makeText(context, "Password change not available", Toast.LENGTH_SHORT).show()
                        return@LuxuryButton
                    }
                    if (newPassword != confirmPassword) {
                        Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                        return@LuxuryButton
                    }
                    if (newPassword.length < 6) {
                        Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                        return@LuxuryButton
                    }

                    isLoading = true
                    scope.launch {
                        try {
                            auth.updateUser {
                                password = newPassword
                            }
                            isLoading = false
                            Toast.makeText(context, "Password updated successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } catch (e: Exception) {
                            isLoading = false
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                isLoading = isLoading,
                modifier = Modifier.height(50.dp)
            )
        }
    }
}
