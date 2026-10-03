package com.nisr.sauservices.ui.location

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.LocationViewModel

@Composable
fun ManualLocationScreen(
    navController: NavController,
    viewModel: LocationViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    var areaQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }

    LuxuryScaffold(
        title = "CHOOSE LOCATION",
        onBackClick = { navController.popBackStack() }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(LuxuryBackground)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Icon(
                imageVector = Icons.Default.LocationCity,
                contentDescription = null,
                tint = LuxuryGold,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Enter Your Area",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = LuxuryTextPrimary
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Enter your area or locality so we can show services available near you.",
                style = MaterialTheme.typography.bodyLarge,
                color = LuxuryTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            LuxuryTextField(
                value = areaQuery,
                onValueChange = { areaQuery = it },
                label = "AREA OR LOCALITY",
                leadingIcon = Icons.Default.LocationCity,
                enabled = !isSearching
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.address != "Fetching address..." && uiState.address != "Error fetching address" && areaQuery.isNotEmpty()) {
                 Text(
                    text = "Matched: ${uiState.address}",
                    color = LuxeAccentSage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            LuxuryButton(
                text = "CONTINUE",
                isLoading = isSearching || uiState.isFetchingAddress,
                onClick = {
                    if (areaQuery.isBlank()) {
                        Toast.makeText(context, "Please enter your area", Toast.LENGTH_SHORT).show()
                    } else {
                        isSearching = true
                        viewModel.searchLocation(areaQuery.trim(), context)
                    }
                }
            )
        }
    }

    // Handle auto-confirm when address is fetched after search
    LaunchedEffect(uiState.address) {
        if (isSearching && !uiState.isFetchingAddress && uiState.address != "Fetching address..." && uiState.address != "Error fetching address") {
            viewModel.confirmLocation(context) {
                isSearching = false
                navController.navigate(Screen.Home) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else if (isSearching && uiState.address == "Error fetching address") {
            isSearching = false
            Toast.makeText(context, "Could not find this location. Please try another area.", Toast.LENGTH_LONG).show()
        }
    }
}
