package com.nisr.sauservices.ui.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.ElectricBike
import androidx.compose.material.icons.rounded.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.nisr.sauservices.ui.components.SauColors
import com.nisr.sauservices.ui.viewmodel.TrackingViewModel
import com.nisr.sauservices.ui.theme.*
import kotlinx.coroutines.launch

private const val MAP_STYLE = """
[
  {
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#212121"
      }
    ]
  },
  {
    "elementType": "labels.icon",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [
      {
        "color": "#212121"
      }
    ]
  },
  {
    "featureType": "administrative",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "featureType": "administrative.country",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#9e9e9e"
      }
    ]
  },
  {
    "featureType": "administrative.land_parcel",
    "stylers": [
      {
        "visibility": "off"
      }
    ]
  },
  {
    "featureType": "administrative.locality",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#bdbdbd"
      }
    ]
  },
  {
    "featureType": "poi",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#181818"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#616161"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "labels.text.stroke",
    "stylers": [
      {
        "color": "#1b1b1b"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "geometry.fill",
    "stylers": [
      {
        "color": "#2c2c2c"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#8a8a8a"
      }
    ]
  },
  {
    "featureType": "road.arterial",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#373737"
      }
    ]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#3c3c3c"
      }
    ]
  },
  {
    "featureType": "road.highway.controlled_access",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#4e4e4e"
      }
    ]
  },
  {
    "featureType": "road.local",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#616161"
      }
    ]
  },
  {
    "featureType": "transit",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#000000"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#3d3d3d"
      }
    ]
  }
]
"""


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    navController: NavController,
    orderId: String,
    viewModel: TrackingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(uiState.partnerLocation, 15f)
    }

    val partnerMarkerState = rememberMarkerState(position = uiState.partnerLocation)

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions.values.any { it }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Initialize tracking logic
    LaunchedEffect(orderId) {
        viewModel.startTracking(orderId)
    }

    // Sync marker and camera when partner moves
    LaunchedEffect(uiState.partnerLocation) {
        partnerMarkerState.position = uiState.partnerLocation
        cameraPositionState.animate(CameraUpdateFactory.newLatLng(uiState.partnerLocation))
    }

    Scaffold(
        containerColor = SauColors.Background,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Track Order", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = SauColors.TextDark)
                        Text("#${orderId.takeLast(6).uppercase()}", fontSize = 12.sp, color = SauColors.TextGrey)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = SauColors.TextDark)
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(uiState.partnerLocation, 16f))
                        }
                    }) {
                        Icon(Icons.Rounded.MyLocation, null, tint = SauColors.Primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SauColors.Background)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission,
                    mapStyleOptions = MapStyleOptions(MAP_STYLE)
                ),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
            ) {
                // Moving Partner Marker (Luxury Gold Custom Marker)
                Marker(
                    state = partnerMarkerState,
                    title = "Delivery Partner",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                )

                // Destination Marker
                uiState.destinationLocation?.let {
                    Marker(
                        state = rememberMarkerState(position = it),
                        title = "Delivery Point",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }
            }

            // Professional Tracking Status Card (Luxury Dark)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = SauColors.Background,
                border = BorderStroke(1.dp, SauColors.Border),
                shadowElevation = 24.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp)) {
                    val isCompleted = uiState.isCompleted
                    
                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                    val pulseAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.4f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1200, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "pulseAlpha"
                    )

                    // Partner Profile Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = SauColors.Surface,
                            border = BorderStroke(1.dp, SauColors.Border)
                        ) {
                            AsyncImage(
                                model = uiState.partnerAvatar,
                                contentDescription = "Partner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.partnerName,
                                color = SauColors.TextDark,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = SauColors.Primary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${uiState.partnerRating} · Delivery Partner",
                                    color = SauColors.TextGrey,
                                    fontSize = 13.sp
                                )
                            }
                        }
                        
                        Row {
                            IconButton(
                                onClick = { /* TODO: Call */ },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SauColors.Surface)
                                    .border(1.dp, SauColors.Border, CircleShape)
                            ) {
                                Icon(Icons.Default.Call, null, tint = SauColors.Primary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            IconButton(
                                onClick = { /* TODO: Chat */ },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SauColors.Surface)
                                    .border(1.dp, SauColors.Border, CircleShape)
                            ) {
                                Icon(Icons.Default.Chat, null, tint = SauColors.Primary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    HorizontalDivider(color = SauColors.Border, thickness = 1.dp, modifier = Modifier.padding(bottom = 20.dp))

                    Text(
                        text = uiState.statusTitle,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = if (isCompleted) SuccessGreen else SauColors.TextDark
                    )
                    Text(
                        text = uiState.statusSubtitle,
                        fontSize = 14.sp,
                        color = SauColors.TextGrey,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Themed Progress Bar
                    LinearProgressIndicator(
                        progress = { uiState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SauColors.Primary,
                        trackColor = SauColors.Surface,
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isCompleted) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(SauColors.Primary)
                                    .graphicsLayer(alpha = pulseAlpha)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Live Tracking Active",
                                fontSize = 13.sp,
                                color = SauColors.Primary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = SuccessGreen
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Service Delivered Successfully",
                                fontSize = 13.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
