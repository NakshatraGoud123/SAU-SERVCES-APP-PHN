 package com.nisr.sauservices.ui.auth

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.AuthState
import com.nisr.sauservices.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var step by remember { mutableIntStateOf(1) }
    var email by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    
    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.navigate(Screen.ResetPassword(email)) {
                popUpTo<Screen.ForgotPassword> { inclusive = true }
            }
        }
        if (authState is AuthState.Error) {
            Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            authViewModel.resetState()
        }
    }

    LuxuryScaffold(
        title = "RECOVERY",
        onBackClick = { 
            if (step > 1) {
                step--
                authViewModel.resetState()
            } else {
                navController.popBackStack()
            }
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

            // Progress Steps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LuxuryProgressStep(1, step >= 1, step > 1)
                Box(modifier = Modifier.width(32.dp).height(1.dp).background(LuxuryBorder))
                LuxuryProgressStep(2, step >= 2, step > 2)
            }

            Spacer(modifier = Modifier.height(48.dp))

            Crossfade(targetState = step, label = "stepTransition") { currentStep ->
                when (currentStep) {
                    1 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(LuxuryCard)
                                .border(1.dp, LuxuryBorder, RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Mail, null, tint = LuxuryGold, modifier = Modifier.size(36.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text("Reset Password", fontSize = 28.sp, fontWeight = FontWeight.Black, color = LuxuryTextPrimary)
                        Text(
                            "Enter your email to receive recovery code",
                            fontSize = 15.sp,
                            color = LuxuryTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        LuxuryTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "EMAIL ADDRESS",
                            leadingIcon = Icons.Rounded.AlternateEmail,
                            keyboardType = KeyboardType.Email,
                            enabled = authState !is AuthState.Loading
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        LuxuryButton(
                            text = "SEND CODE",
                            onClick = { authViewModel.sendPasswordReset(email); step = 2 },
                            isLoading = authState is AuthState.Loading,
                            enabled = email.isNotBlank()
                        )
                    }
                    2 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(LuxuryCard)
                                .border(1.dp, LuxuryBorder, RoundedCornerShape(24.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.VpnKey, null, tint = LuxuryGold, modifier = Modifier.size(36.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Text("Verify Code", fontSize = 28.sp, fontWeight = FontWeight.Black, color = LuxuryTextPrimary)
                        Text(
                            "We sent a code to $email",
                            fontSize = 15.sp,
                            color = LuxuryTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        LuxuryTextField(
                            value = otpCode,
                            onValueChange = { if (it.length <= 6) otpCode = it },
                            label = "6-DIGIT CODE",
                            leadingIcon = Icons.Rounded.VpnKey,
                            keyboardType = KeyboardType.Number,
                            enabled = authState !is AuthState.Loading
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        LuxuryButton(
                            text = "VERIFY",
                            onClick = { authViewModel.verifyPasswordResetOtp(email, otpCode) },
                            isLoading = authState is AuthState.Loading,
                            enabled = otpCode.length >= 6
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        TextButton(onClick = { authViewModel.sendPasswordReset(email) }) {
                            Text("Resend Code", color = LuxuryGold, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LuxuryProgressStep(number: Int, isActive: Boolean, isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isActive) LuxuryGold else LuxuryCard)
            .border(1.dp, if (isActive) LuxuryGold else LuxuryBorder, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(Icons.Rounded.Check, null, tint = LuxuryBackground, modifier = Modifier.size(20.dp))
        } else {
            Text(
                text = number.toString(),
                color = if (isActive) LuxuryBackground else LuxuryTextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
