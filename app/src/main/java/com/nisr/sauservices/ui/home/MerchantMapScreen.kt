package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.VendorsViewModel
import com.nisr.sauservices.ui.viewmodel.VendorsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantMapScreen(
    navController: NavController,
    viewModel: VendorsViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(17.4485, 78.3910), 13f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nearby Boutiques", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                if (uiState is VendorsUiState.Success) {
                    val vendors = (uiState as VendorsUiState.Success).vendors
                    vendors.forEach { vendor ->
                        val lat = vendor.latitude ?: (17.4485 + ((vendor.id.hashCode() % 50) * 0.001))
                        val lng = vendor.longitude ?: (78.3910 + ((vendor.id.hashCode() % 50) * 0.001))
                        val vendorLatLng = LatLng(lat, lng)

                        Marker(
                            state = MarkerState(position = vendorLatLng),
                            title = vendor.displayName,
                            snippet = "${vendor.displayCategory} · ${vendor.displayRating} Star",
                            onClick = {
                                navController.navigate(Screen.MerchantShop(vendor.id))
                                true
                            }
                        )
                    }
                }
            }

            // Optional: Floating list/card at bottom could be added here
        }
    }
}
