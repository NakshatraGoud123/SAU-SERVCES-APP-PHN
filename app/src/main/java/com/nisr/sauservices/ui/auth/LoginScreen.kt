package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.launch
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

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
                sessionManager.saveUserRole("customer") // Default role
                navController.navigate(Screen.Home) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(
                    context,
                    state.message,
                    Toast.LENGTH_LONG
                ).show()
                authViewModel.resetState()
            }
            else -> {
                // Idle / Loading
            }
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
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(28.dp),
                color = LuxuryCard,
                border = BorderStroke(1.dp, LuxuryBorder)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.sau_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize().graphicsLayer(alpha = 0.9f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = LuxuryTextPrimary,
                    fontFamily = FontFamily.Serif
                )
            )
            Text(
                text = "Sign in to your member account",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = LuxuryTextSecondary
                )
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Luxury Form
            Column(modifier = Modifier.fillMaxWidth()) {
                LuxuryTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "EMAIL",
                    leadingIcon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                                tint = LuxuryGold
                            )
                        }
                    }
                )

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    TextButton(onClick = { navController.navigate(Screen.ForgotPassword) }) {
                        Text("Forgot Password?", color = LuxuryGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                LuxuryButton(
                    text = "SIGN IN",
                    onClick = { authViewModel.signIn(email, password) },
                    isLoading = authState is AuthState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))

                // OR Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = LuxuryBorder)
                    Text(
                        " OR ", 
                        color = LuxuryTextSecondary, 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = LuxuryBorder)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Google Button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            try {
                                val idToken = GoogleSignInUtils.launchGoogleSignIn(context)
                                if (idToken != null) {
                                    authViewModel.signInWithGoogle(idToken)
                                }
                            } catch (e: Exception) {
                                if (e !is androidx.credentials.exceptions.GetCredentialCancellationException) {
                                    Toast.makeText(context, "Google Sign In Failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, LuxuryBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LuxuryTextPrimary)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google),
                            contentDescription = "Google",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Sign in with Google", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("New to SAU?", color = LuxuryTextSecondary, fontSize = 14.sp)
                    TextButton(onClick = { navController.navigate(Screen.Register("customer")) }) {
                        Text("Join Now", color = LuxuryGold, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
