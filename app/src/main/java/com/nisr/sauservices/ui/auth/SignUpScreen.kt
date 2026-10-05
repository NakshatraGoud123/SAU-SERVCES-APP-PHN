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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.nisr.sauservices.R
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
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
    val securityState by authViewModel.securityState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            password = "" // SECURE: Clear password
            val user = authViewModel.currentUser
            if (user != null) {
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
            } else {
                Toast.makeText(context, "Welcome! Please confirm your account via email.", Toast.LENGTH_LONG).show()
                navController.navigate(Screen.Login()) {
                    popUpTo(0) { inclusive = true }
                }
            }
            authViewModel.resetState()
        } else if (authState is AuthState.Error) {
            password = "" // SECURE: Clear password on error
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        // 1. BACKGROUND DECORATIONS (Matching Login)
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

            // Top Right Light Blue Circle
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

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = LuxeTextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
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
                Spacer(modifier = Modifier.height(8.dp))

                // 2. PREMIUM LOGO
                Surface(
                    modifier = Modifier.size(90.dp),
                    shape = RoundedCornerShape(24.dp),
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
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // 3. HEADINGS
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Join ",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF1A2A47),
                            fontFamily = FontFamily.Serif
                        )
                    )
                    Text(
                        text = "SAU",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            color = LuxeGold,
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
                Text(
                    text = "Create your premium member account",
                    style = MaterialTheme.typography.bodyLarge.copy(color = LuxeTextSecondary),
                    textAlign = TextAlign.Center
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

                Spacer(modifier = Modifier.height(40.dp))

                // 4. MAIN FORM CARD
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    color = Color.White,
                    shadowElevation = 6.dp
                ) {
                    Column(modifier = Modifier.padding(28.dp)) {
                        if (authState is AuthState.Error) {
                            Text(
                                text = (authState as AuthState.Error).message,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }

                        // Name Field
                        LabelWithIcon("Full Name", Icons.Default.Person)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter your full name", color = Color.LightGray) },
                            leadingIcon = { Icon(Icons.Outlined.Person, null, tint = Color(0xFF1A2A47)) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE1E8F0),
                                focusedBorderColor = Color(0xFF1A2A47)
                            ),
                            singleLine = true,
                            enabled = authState !is AuthState.Loading
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Email Field
                        LabelWithIcon("Email", Icons.Default.Email)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter your email address", color = Color.LightGray) },
                            leadingIcon = { Icon(Icons.Outlined.AlternateEmail, null, tint = Color(0xFF1A2A47)) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE1E8F0),
                                focusedBorderColor = Color(0xFF1A2A47)
                            ),
                            singleLine = true,
                            enabled = authState !is AuthState.Loading
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Password Field
                        LabelWithIcon("Password", Icons.Default.Lock)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Enter a secure password", color = Color.LightGray) },
                            leadingIcon = { Icon(Icons.Outlined.Lock, null, tint = Color(0xFF1A2A47)) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        null, 
                                        tint = Color.Gray, 
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = Color(0xFFE1E8F0),
                                focusedBorderColor = Color(0xFF1A2A47)
                            ),
                            singleLine = true,
                            enabled = authState !is AuthState.Loading
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // 5. GRADIENT CREATE ACCOUNT BUTTON
                        Button(
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.PersonAdd, null, tint = Color.White)
                                        Spacer(Modifier.width(12.dp))
                                        Text("CREATE ACCOUNT", color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // 6. Sign In Link
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Member already?", color = LuxeTextSecondary, fontSize = 15.sp)
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("SIGN IN", color = LuxeGold, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // 7. FOOTER
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
}

@Composable
private fun LabelWithIcon(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFF1A2A47), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, color = Color(0xFF1A2A47), fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
