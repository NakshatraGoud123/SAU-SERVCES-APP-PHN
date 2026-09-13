package com.nisr.sauservices.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.api.SupabaseClient
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAccountsScreen(navController: NavController) {
    val auth = SupabaseClient.client.auth
    val user = auth.currentUserOrNull()
    val context = LocalContext.current

    // In Supabase, identities represent linked accounts (google, email, phone, etc.)
    val identities = user?.identities?.map { it.provider } ?: emptyList()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Linked Accounts", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Securely connect your social and communication accounts.",
                fontSize = 14.sp,
                color = LuxeTextSecondary,
                fontWeight = FontWeight.Medium
            )

            AccountLinkItem(
                icon = Icons.Default.Email,
                title = "Email",
                isLinked = identities.contains("email"),
                onClick = { 
                    Toast.makeText(context, "Email linking is managed via Supabase Auth", Toast.LENGTH_SHORT).show()
                }
            )

            AccountLinkItem(
                icon = Icons.Default.Phone,
                title = "Phone Number",
                isLinked = identities.contains("phone"),
                onClick = {
                    Toast.makeText(context, "Phone linking feature coming soon", Toast.LENGTH_SHORT).show()
                }
            )

            AccountLinkItem(
                icon = Icons.Default.Link,
                title = "Google Account",
                isLinked = identities.contains("google"),
                onClick = {
                    Toast.makeText(context, "Google linking feature coming soon", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

@Composable
fun AccountLinkItem(icon: ImageVector, title: String, isLinked: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, LuxeBorder)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(LuxeHighlightChampagne.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = LuxeTextPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(16.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f), color = LuxeTextPrimary)
            
            if (isLinked) {
                Text("Linked", color = Color(0xFF4CAF50), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            } else {
                Text("Link", color = LuxeAccentSage, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}
