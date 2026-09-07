package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontFamily
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.R
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

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
                // If user is already logged in (Email confirmation OFF)
                sessionManager.saveLoginState(true)
                sessionManager.saveUserRole("customer")
                navController.navigate(Screen.Home) {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                // If email confirmation is ON, Supabase doesn't log them in yet
                Toast.makeText(context, "Registration successful! Please check your email to confirm your account.", Toast.LENGTH_LONG).show()
                navController.navigate(Screen.Login()) {
                    popUpTo(0) { inclusive = true }
                }
            }
            authViewModel.resetState()
        }
    }

    LuxuryScaffold(
        title = "",
        onBackClick = { navController.popBackStack() }
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

            // Premium Cinematic Branding
            Surface(
                modifier = Modifier.size(90.dp),
                shape = RoundedCornerShape(24.dp),
                color = LuxuryCard,
                border = BorderStroke(1.dp, LuxuryBorder)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.sau_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize().graphicsLayer(alpha = 0.9f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Join SAU",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    color = LuxuryTextPrimary,
                    fontFamily = FontFamily.Serif
                )
            )
            Text(
                text = "Create your premium member account",
                style = MaterialTheme.typography.bodyLarge.copy(color = LuxuryTextSecondary),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Registration Form
            Column(modifier = Modifier.fillMaxWidth()) {
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
                    label = "SECURE PASSWORD",
                    leadingIcon = Icons.Rounded.Lock,
                    enabled = authState !is AuthState.Loading,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                if (passwordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                null, 
                                tint = LuxuryGold, 
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(48.dp))

                LuxuryButton(
                    text = "CREATE ACCOUNT",
                    onClick = { 
                        if (email.isNotBlank() && password.isNotBlank()) {
                            val userData = mapOf<String, Any?>(
                                "full_name" to fullName,
                                "email" to email,
                                "user_type" to role
                            )
                            authViewModel.signUp(email, password, userData)
                        } else {
                            Toast.makeText(context, "Email and password are required", Toast.LENGTH_SHORT).show()
                        }
                    },
                    isLoading = authState is AuthState.Loading
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Sign In Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Member already?", color = LuxuryTextSecondary, fontSize = 15.sp)
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("SIGN IN", color = LuxuryGold, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
