package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.R
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

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
fun SignUpScreen(
    navController: NavController, 
    role: String = "customer", 
    authViewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            val user = authViewModel.currentUser
            if (user != null) {
                sessionManager.saveLoginState(true)
                sessionManager.saveUserRole("customer")
                navController.navigate(Screen.Home) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                Toast.makeText(context, "Welcome! Please confirm your account via email.", Toast.LENGTH_LONG).show()
                navController.navigate(Screen.Login()) {
                    popUpTo(0) { inclusive = true }
                }
            }
            authViewModel.resetState()
        }
    }

    Scaffold(
        containerColor = LuxeBackground,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(24.dp),
                color = LuxeCard,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.sau_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Join SAU",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    color = LuxeTextPrimary,
                    fontFamily = FontFamily.Serif
                )
            )
            Text(
                text = "Create your professional profile",
                style = MaterialTheme.typography.bodyLarge.copy(color = LuxeTextSecondary),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = LuxeCard,
                border = BorderStroke(1.dp, LuxeBorder),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    if (authState is AuthState.Error) {
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    LuxuryTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "FULL NAME",
                        leadingIcon = Icons.Rounded.Person,
                        enabled = authState !is AuthState.Loading
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LuxuryTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "EMAIL ADDRESS",
                        leadingIcon = Icons.Rounded.AlternateEmail,
                        enabled = authState !is AuthState.Loading
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LuxuryTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "PASSWORD",
                        leadingIcon = Icons.Rounded.Lock,
                        enabled = authState !is AuthState.Loading,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    null, 
                                    tint = LuxeAccentSage, 
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    LuxuryButton(
                        text = "CREATE ACCOUNT",
                        onClick = { 
                            if (email.isNotBlank() && password.isNotBlank()) {
                                val userData = mapOf<String, Any?>(
                                    "name" to fullName,
                                    "email" to email,
                                    "role" to role
                                )
                                authViewModel.signUp(email, password, userData)
                            } else {
                                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                            }
                        },
                        isLoading = authState is AuthState.Loading,
                        modifier = Modifier.height(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Already a member?", color = LuxeTextSecondary, fontSize = 15.sp)
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("SIGN IN", color = LuxeAccentSage, fontWeight = FontWeight.Black, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
