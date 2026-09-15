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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Chat
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.*
import com.google.maps.android.compose.*
import com.nisr.sauservices.ui.viewmodel.TrackingViewModel
import com.nisr.sauservices.ui.theme.*
import kotlinx.coroutines.launch

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
private val LuxeGold = Color(0xFFE8C66A)

private const val MAP_STYLE = """
[
  {
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#f5f5f5"
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
        "color": "#616161"
      }
    ]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [
      {
        "color": "#f5f5f5"
      }
    ]
  },
  {
    "featureType": "administrative.land_parcel",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#bdbdbd"
      }
    ]
  },
  {
    "featureType": "poi",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#eeeeee"
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
        "color": "#e5e5e5"
      }
    ]
  },
  {
    "featureType": "poi.park",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#9e9e9e"
      }
    ]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#ffffff"
      }
    ]
  },
  {
    "featureType": "road.arterial",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#757575"
      }
    ]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#dadada"
      }
    ]
  },
  {
    "featureType": "road.highway",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#616161"
      }
    ]
  },
  {
    "featureType": "road.local",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#9e9e9e"
      }
    ]
  },
  {
    "featureType": "transit.line",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#e5e5e5"
      }
    ]
  },
  {
    "featureType": "transit.station",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#eeeeee"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [
      {
        "color": "#c9c9c9"
      }
    ]
  },
  {
    "featureType": "water",
    "elementType": "labels.text.fill",
    "stylers": [
      {
        "color": "#9e9e9e"
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
    val animatedLat by animateFloatAsState(targetValue = uiState.partnerLocation.latitude.toFloat(), label = "lat")
    val animatedLng by animateFloatAsState(targetValue = uiState.partnerLocation.longitude.toFloat(), label = "lng")
    val animatedLocation = LatLng(animatedLat.toDouble(), animatedLng.toDouble())

    LaunchedEffect(animatedLocation) {
        partnerMarkerState.position = animatedLocation
        
        // Auto-zoom to fit both locations
        val boundsBuilder = LatLngBounds.builder()
        boundsBuilder.include(animatedLocation)
        uiState.destinationLocation?.let { boundsBuilder.include(it) }
        
        try {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 200),
                1000
            )
        } catch (e: Exception) {
            // Fallback if bounds calculation fails initially
            cameraPositionState.animate(CameraUpdateFactory.newLatLng(animatedLocation))
        }
    }

    Scaffold(
        containerColor = LuxeBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Live Tracking", fontSize = 18.sp, fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif)
                        Text("#${orderId.takeLast(6).uppercase()}", fontSize = 11.sp, color = LuxeTextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                actions = {
                    IconButton(
                        onClick = { 
                            scope.launch {
                                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(uiState.partnerLocation, 16f))
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp).background(LuxeHighlightChampagne.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(Icons.Rounded.MyLocation, null, tint = LuxeAccentSage, modifier = Modifier.size(20.dp))
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
                properties = MapProperties(
                    isMyLocationEnabled = hasLocationPermission,
                    mapStyleOptions = MapStyleOptions(MAP_STYLE)
                ),
                uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false)
            ) {
                // Moving Partner Marker (Luxe Azure Custom Marker)
                Marker(
                    state = partnerMarkerState,
                    title = "Delivery Partner",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                // Destination Marker
                uiState.destinationLocation?.let {
                    Marker(
                        state = rememberMarkerState(position = it),
                        title = "Your Location",
                        icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                    )
                }
            }

            // Professional Tracking Status Card (Luxe Daylight)
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = LuxeCard,
                border = BorderStroke(1.dp, LuxeBorder),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val isCompleted = uiState.isCompleted
                    
                    // ETA Badge (Animated)
                    if (!isCompleted && uiState.etaMinutes > 0) {
                        Surface(
                            modifier = Modifier.padding(bottom = 20.dp),
                            color = LuxeHighlightChampagne,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, LuxeGold.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CheckCircle, null, tint = LuxeAccentSage, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Arriving in ${uiState.etaMinutes} mins",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LuxeTextPrimary,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

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
                            .padding(bottom = 28.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(60.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = LuxeHighlightChampagne,
                            border = BorderStroke(1.dp, LuxeBorder)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(uiState.partnerAvatar)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Partner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.partnerName,
                                color = LuxeTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = LuxeGold, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${uiState.partnerRating} · Personal Assistant",
                                    color = LuxeTextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        Row {
                            Surface(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { /* TODO: Call */ },
                                shape = CircleShape,
                                color = LuxeHighlightChampagne.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, LuxeBorder)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.Call, null, tint = LuxeTextPrimary, modifier = Modifier.size(20.dp))
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Surface(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { 
                                        navController.navigate(com.nisr.sauservices.ui.Screen.Chat(
                                            orderId = orderId,
                                            receiverId = "partner_id_here", 
                                            receiverName = uiState.partnerName
                                        ))
                                    },
                                shape = CircleShape,
                                color = LuxeHighlightChampagne.copy(alpha = 0.4f),
                                border = BorderStroke(1.dp, LuxeBorder)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.Chat, null, tint = LuxeTextPrimary, modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = LuxeBorder, thickness = 1.dp, modifier = Modifier.padding(bottom = 24.dp))

                    Text(
                        text = uiState.statusTitle,
                        fontWeight = FontWeight.Black,
                        fontSize = 24.sp,
                        color = if (isCompleted) SuccessGreen else LuxeTextPrimary,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = uiState.statusSubtitle,
                        fontSize = 14.sp,
                        color = LuxeTextSecondary,
                        modifier = Modifier.padding(top = 4.dp),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Luxe Progress Bar
                    LinearProgressIndicator(
                        progress = { uiState.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = LuxeAccentSage,
                        trackColor = LuxeBorder,
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))

                    // LIVE TIMELINE
                    if (uiState.timeline.isNotEmpty()) {
                        LuxeSectionHeader("Order Journey", null)
                        Spacer(Modifier.height(16.dp))
                        uiState.timeline.forEachIndexed { index, item ->
                            TimelineRow(
                                item = item,
                                isLast = index == uiState.timeline.size - 1
                            )
                        }
                        Spacer(Modifier.height(24.dp))
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isCompleted) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(LuxeAccentSage)
                                    .graphicsLayer(alpha = pulseAlpha)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "LIVE TRACKING ACTIVE",
                                fontSize = 12.sp,
                                color = LuxeAccentSage,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = SuccessGreen
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "DELIVERED SUCCESSFULLY",
                                fontSize = 12.sp,
                                color = SuccessGreen,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(item: com.nisr.sauservices.ui.viewmodel.TimelineStatus, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(if (item.isDone) LuxeAccentSage else LuxeBorder)
                    .border(2.dp, if (item.isCurrent) LuxeGold else Color.Transparent, CircleShape)
            )
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(if (item.isDone) LuxeAccentSage.copy(alpha = 0.5f) else LuxeBorder)
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = 20.dp)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = if (item.isCurrent) FontWeight.Black else FontWeight.Bold,
                color = if (item.isDone) LuxeTextPrimary else LuxeTextSecondary
            )
        }
    }
}

@Composable
private fun LuxeSectionHeader(title: String, onActionClick: (() -> Unit)? = null) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = LuxeTextSecondary, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
    }
}
