package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

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

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { com.nisr.sauservices.data.local.SessionManager(context) }
    
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                sessionManager.saveLoginState(true)
                sessionManager.saveUserRole("customer")
                navController.navigate(Screen.Home) {
                    popUpTo(0) { inclusive = true }
                }
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = LuxeBackground,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
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
            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(28.dp),
                color = LuxeCard,
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(20.dp)) {
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
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = LuxeTextPrimary,
                    fontFamily = FontFamily.Serif
                )
            )
            Text(
                text = "Sign in to your member account",
                style = MaterialTheme.typography.bodyLarge.copy(color = LuxeTextSecondary)
            )

            Spacer(modifier = Modifier.height(56.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = LuxeCard,
                border = BorderStroke(1.dp, LuxeBorder),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    LuxuryTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "EMAIL",
                        leadingIcon = Icons.Default.Email
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LuxuryTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = "PASSWORD",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = LuxeAccentSage
                                )
                            }
                        }
                    )

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = { navController.navigate(Screen.ForgotPassword) }) {
                            Text("Forgot Password?", color = LuxeAccentSage, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    LuxuryButton(
                        text = "SIGN IN",
                        onClick = { authViewModel.signIn(email, password) },
                        isLoading = authState is AuthState.Loading,
                        modifier = Modifier.height(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = LuxeBorder)
                Text(" OR ", color = LuxeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                HorizontalDivider(modifier = Modifier.weight(1f), color = LuxeBorder)
            }

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedButton(
                onClick = {
                    scope.launch {
                        try {
                            val idToken = GoogleSignInUtils.launchGoogleSignIn(context)
                            if (idToken != null) authViewModel.signInWithGoogle(idToken)
                        } catch (e: Exception) {
                            if (e !is androidx.credentials.exceptions.GetCredentialCancellationException) {
                                Toast.makeText(context, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, LuxeBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxeTextPrimary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(id = R.drawable.ic_google), contentDescription = "Google", modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Sign in with Google", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("New to SAU?", color = LuxeTextSecondary, fontSize = 14.sp)
                TextButton(onClick = { navController.navigate(Screen.Register("customer")) }) {
                    Text("Join Now", color = LuxeAccentSage, fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
