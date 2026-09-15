package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontFamily
import kotlinx.coroutines.launch
import com.nisr.sauservices.R
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navController: NavController,
    role: String = "customer",
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val authState by authViewModel.authState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                sessionManager.saveLoginState(true)
                sessionManager.saveUserRole(role)
                navController.navigate(Screen.Home) {
                    popUpTo(0) { inclusive = true }
                }
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
                authViewModel.resetState()
            }
            else -> Unit
        }
    }

    LuxuryScaffold(
        title = "",
        onBackClick = null // Start of auth flow usually doesn't have back
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
            Spacer(modifier = Modifier.height(40.dp))

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
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().graphicsLayer(alpha = 0.9f),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "SAU SOLUTIONS",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = LuxuryGold,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp
                )
            )
            
            Text(
                text = "Premium Solutions at Your Fingertips",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = LuxuryTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(56.dp))

            // Sign In Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .background(LuxuryCard)
                    .border(1.dp, LuxuryBorder, RoundedCornerShape(32.dp))
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Let's Get Started",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = LuxuryTextPrimary,
                        fontSize = 22.sp
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in to your account",
                    style = MaterialTheme.typography.bodySmall.copy(color = LuxuryTextSecondary),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                LuxuryTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "EMAIL ADDRESS",
                    leadingIcon = Icons.Rounded.Email
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

                Spacer(modifier = Modifier.height(32.dp))

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

                // Sign Up Link
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

            // Footer
            Image(
                painter = painterResource(id = R.drawable.  drawable_illustration1),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(100.dp).graphicsLayer(alpha = 0.5f),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = buildAnnotatedString {
                    append("By continuing, you agree to our ")
                    withStyle(SpanStyle(color = LuxuryGold, fontWeight = FontWeight.Bold)) { append("Terms") }
                    append(" and ")
                    withStyle(SpanStyle(color = LuxuryGold, fontWeight = FontWeight.Bold)) { append("Privacy Policy") }
                },
                style = MaterialTheme.typography.labelSmall.copy(textAlign = TextAlign.Center, color = LuxuryTextSecondary),
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
