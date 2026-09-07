package com.nisr.sauservices.ui.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.nisr.sauservices.ui.viewmodel.LocationViewModel
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationPickerScreen(
    navController: NavController,
    viewModel: LocationViewModel = viewModel(),
) {
    val context = LocalContext.current
    val uiState = viewModel.uiState
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.centerLocation, 15f)
    }

    var searchQuery by remember { mutableStateOf("") }

    var hasLocationPermission by remember {
        mutableStateOf(
            (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED) ||
            (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        val isGranted = permissions.values.any { it }
        hasLocationPermission = isGranted
        if (isGranted) {
            viewModel.getCurrentLocation(context)
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        } else {
            viewModel.getCurrentLocation(context)
        }
    }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            viewModel.updateCenterLocation(cameraPositionState.position.target, context)
        }
    }

    LaunchedEffect(uiState.centerLocation) {
        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(uiState.centerLocation, 15f))
    }

    LuxuryScaffold(
        title = "SET LOCATION",
        onBackClick = { navController.popBackStack() }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(myLocationButtonEnabled = false, zoomControlsEnabled = false)
            )

            // Center Pin
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Pin",
                        tint = ErrorRed,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Search Bar
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = LuxuryCard),
                border = BorderStroke(1.dp, LuxuryBorder)
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search for area, street...", color = LuxuryTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = LuxuryGold) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchLocation(searchQuery, context) }) {
                                Icon(Icons.Default.MyLocation, null, tint = LuxuryGold)
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { viewModel.searchLocation(searchQuery, context) }),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = LuxuryTextPrimary,
                        unfocusedTextColor = LuxuryTextPrimary
                    ),
                    singleLine = true
                )
            }

            // My Location FAB
            FloatingActionButton(
                onClick = { 
                    if (hasLocationPermission) viewModel.getCurrentLocation(context) 
                    else launcher.launch(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                    )
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 260.dp, end = 16.dp),
                containerColor = LuxuryCard,
                contentColor = LuxuryGold
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }

            // Bottom Confirm Sheet
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = LuxuryCard,
                border = BorderStroke(1.dp, LuxuryBorder)
            ) {
                Column(modifier = Modifier.padding(28.dp)) {
                    Text(
                        "CONFIRM LOCATION", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = LuxuryGold,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(LuxuryGold.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.LocationOn, 
                                contentDescription = null, 
                                tint = LuxuryGold, 
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = if (uiState.isFetchingAddress) "Locating..." else uiState.landmark.ifEmpty { "Pinned Location" }, 
                                fontWeight = FontWeight.Black, 
                                fontSize = 18.sp,
                                color = LuxuryTextPrimary
                            )
                            Text(
                                text = if (uiState.isFetchingAddress) "Updating address..." else uiState.address, 
                                color = LuxuryTextSecondary, 
                                fontSize = 13.sp, 
                                maxLines = 2,
                                lineHeight = 18.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(36.dp))
                    LuxuryButton(
                        text = "CONFIRM LOCATION",
                        onClick = { 
                            viewModel.confirmLocation(context) { 
                                navController.popBackStack() 
                            } 
                        },
                        isLoading = uiState.isFetchingAddress
                    )
                }
            }
        }
    }
}
