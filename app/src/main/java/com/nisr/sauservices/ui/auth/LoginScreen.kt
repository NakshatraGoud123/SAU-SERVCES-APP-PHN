package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.nisr.sauservices.R
import com.nisr.sauservices.data.local.SessionManager
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
private val LuxeTextPrimary = Color(0xFF1A2A47) // Navy Blue
private val LuxeTextSecondary = Color(0xFF8A94A6)
private val LuxeGold = Color(0xFFE8C66A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState by authViewModel.authState.collectAsState()
    val securityState by authViewModel.securityState.collectAsState()
    
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                password = "" // SECURE: Clear password
                sessionManager.saveLoginState(true)
                sessionManager.saveUserRole("customer")
                
                val hasPermission = ContextCompat.checkSelfPermission(
                    context, 
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                
                if (hasPermission) {
                    navController.navigate(Screen.Home) {
                        popUpTo(0) { inclusive = true }
                    }
                } else {
                    navController.navigate(Screen.LocationPermission) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                authViewModel.resetState()
            }
            is AuthState.Error -> {
                password = "" // SECURE: Clear password on error
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                authViewModel.resetState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 1. BACKGROUND DECORATIONS
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            // Top Left Navy Wave
            val path1 = Path().apply {
                moveTo(0f, height * 0.15f)
                quadraticTo(width * 0.1f, height * 0.08f, width * 0.35f, 0f)
                lineTo(0f, 0f)
                close()
            }
            drawPath(path1, Color(0xFF1A2A47))

            // Top Right Light Blue Circles/Lines (Abstract)
            drawCircle(
                color = Color(0xFFE1E8F0),
                radius = 150f,
                center = Offset(width * 0.85f, height * 0.05f)
            )

            // Bottom Navy Wave
            val pathBottom = Path().apply {
                moveTo(0f, height)
                lineTo(width, height)
                lineTo(width, height * 0.85f)
                quadraticTo(width * 0.7f, height * 0.92f, width * 0.4f, height * 0.88f)
                quadraticTo(width * 0.1f, height * 0.84f, 0f, height * 0.92f)
                close()
            }
            drawPath(pathBottom, Color(0xFF1A2A47))

            // Bottom Gold Line
            val pathGold = Path().apply {
                moveTo(0f, height * 0.9f)
                quadraticTo(width * 0.2f, height * 0.85f, width * 0.5f, height * 0.88f)
                quadraticTo(width * 0.8f, height * 0.91f, width, height * 0.86f)
            }
            drawPath(
                path = pathGold,
                color = LuxeGold,
                style = Stroke(width = 6f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 2. PREMIUM LOGO
            Surface(
                modifier = Modifier.size(110.dp),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xFF1A2A47),
                shadowElevation = 12.dp
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(12.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.sau_logo),
                        contentDescription = "Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 3. HEADINGS
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Welcome ",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1A2A47),
                        fontFamily = FontFamily.Serif
                    )
                )
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        color = LuxeGold,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
            Text(
                text = "Sign in to your member account",
                style = MaterialTheme.typography.bodyLarge.copy(color = LuxeTextSecondary),
                modifier = Modifier.padding(top = 4.dp)
            )

            // Small Page Indicator style divider
            Row(
                modifier = Modifier.padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.width(30.dp).height(2.dp).background(Color(0xFFDDE2EC)))
                Box(Modifier.width(15.dp).height(4.dp).background(LuxeGold, CircleShape))
                Box(Modifier.width(30.dp).height(2.dp).background(Color(0xFFDDE2EC)))
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 4. MAIN FORM CARD
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(32.dp),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(28.dp)) {
                    // Email Field
                    LabelWithIcon("Email", Icons.Default.Email)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your email address", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Color(0xFF1A2A47)) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE1E8F0),
                            focusedBorderColor = Color(0xFF1A2A47)
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Password Field
                    LabelWithIcon("Password", Icons.Default.Lock)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter your password", color = Color.LightGray) },
                        leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Color(0xFF1A2A47)) },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedBorderColor = Color(0xFFE1E8F0),
                            focusedBorderColor = Color(0xFF1A2A47)
                        ),
                        singleLine = true
                    )

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        TextButton(onClick = { navController.navigate(Screen.ForgotPassword) }) {
                            Text("Forgot Password?", color = Color(0xFF3498DB), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. GRADIENT SIGN IN BUTTON
                    Button(
                        onClick = { authViewModel.signIn(email, password) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        ),
                        enabled = authState !is AuthState.Loading && !securityState.isLockedOut
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFF39C12), Color(0xFFF1C40F))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (authState is AuthState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                val buttonText = if (securityState.isLockedOut) "LOCKED (${securityState.remainingLockoutSeconds}s)" else "SIGN IN"
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.AutoMirrored.Filled.Login, null, tint = Color.White)
                                    Spacer(Modifier.width(12.dp))
                                    Text(buttonText, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 6. DIVIDER
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDE2EC))
                Text(" OR ", color = LuxeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFDDE2EC))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 7. GOOGLE BUTTON
            OutlinedButton(
                onClick = {
                    scope.launch {
                        try {
                            val idToken = GoogleSignInUtils.launchGoogleSignIn(context)
                            if (idToken != null) authViewModel.signInWithGoogle(idToken)
                        } catch (e: Exception) {
                            if (e !is GetCredentialCancellationException) {
                                Toast.makeText(context, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFDDE2EC)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google",
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(16.dp))
                    Box(Modifier.width(1.dp).height(24.dp).background(Color(0xFFDDE2EC)))
                    Spacer(Modifier.width(16.dp))
                    Text(
                        "Sign in with Google", 
                        color = Color(0xFF1A2A47), 
                        fontWeight = FontWeight.Bold, 
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 8. JOIN NOW
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("New to SAU?", color = LuxeTextSecondary, fontSize = 14.sp)
                TextButton(onClick = { navController.navigate(Screen.Register("customer")) }) {
                    Text("Join Now", color = Color(0xFFF39C12), fontWeight = FontWeight.Black, fontSize = 14.sp)
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))

            // 9. FOOTER
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp)) {
                Box(Modifier.width(40.dp).height(1.dp).background(Color(0xFFDDE2EC)))
                Text(
                    " TOGETHER FOR A BETTER TOMORROW ", 
                    color = Color.Black.copy(alpha = 0.5f), 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Box(Modifier.width(40.dp).height(1.dp).background(Color(0xFFDDE2EC)))
            }
        }
    }
}

@Composable
private fun LabelWithIcon(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFF1A2A47), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color(0xFF1A2A47), fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
