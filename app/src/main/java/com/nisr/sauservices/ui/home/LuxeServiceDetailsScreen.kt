package com.nisr.sauservices.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*

// ============================================================
// LUXE BRAND COLORS
// ============================================================
private val LuxeBackground = Color(0xFFFDFBFA)
private val LuxeCard = Color(0xFFFFFFFF)
private val LuxeTextPrimary = Color(0xFF423F3D)
private val LuxeTextSecondary = Color(0xFF8D7F77)
private val LuxeAccentSage = Color(0xFF96A68F)
private val LuxeHighlightChampagne = Color(0xFFF5E6D3)
private val LuxeBorder = Color(0xFFEFE9E4)
private val LuxeGold = Color(0xFFE8C66A)
private val SuccessGreen = Color(0xFF22C55E)
private val PkPink = Color(0xFFFF3366)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuxeServiceDetailsScreen(
    navController: NavController,
    serviceId: String,
    serviceName: String = "Plumbing Service"
) {
    var selectedPackage by remember { mutableStateOf("Basic") }
    
    val packagePrices = mapOf(
        "Basic" to 299.0,
        "Standard" to 499.0,
        "Premium" to 799.0
    )

    val currentPrice = packagePrices[selectedPackage] ?: 299.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Details", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = "Favorite", tint = LuxeTextPrimary)
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LuxeCard,
                border = BorderStroke(1.dp, LuxeBorder),
                shadowElevation = 16.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Price", color = LuxeTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(2.dp))
                        Text("₹${currentPrice.toInt()}", color = LuxeTextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        Text("($selectedPackage Service)", color = LuxeAccentSage, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            navController.navigate(Screen.ResidentialBookingDetails("", serviceId))
                        },
                        modifier = Modifier
                            .height(52.dp)
                            .width(180.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PkPink)
                    ) {
                        Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Book Now", fontWeight = FontWeight.Black, fontSize = 15.sp)
                    }
                }
            }
        },
        containerColor = LuxeBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // 1. Service Hero Image with Verified Badge
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=800&auto=format&fit=crop&q=80")
                            .crossfade(true)
                            .build(),
                        contentDescription = serviceName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Verified Badge
                    Surface(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopStart),
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Verified, null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Verified Professional", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = LuxeTextPrimary)
                        }
                    }

                    // Image Counter Pill
                    Surface(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomEnd),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Text(
                            "1/5",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // 2. Service Information & Rating
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LuxeCard)
                        .padding(20.dp)
                ) {
                    Text(
                        text = serviceName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = LuxeTextPrimary,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Leak Repair • Pipe Fitting • Installation",
                        fontSize = 13.sp,
                        color = LuxeTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = LuxeGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("4.8", fontWeight = FontWeight.Black, fontSize = 14.sp, color = LuxeTextPrimary)
                            Text(" (124 reviews)", fontSize = 13.sp, color = LuxeTextSecondary)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = LuxeAccentSage, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("2.3 km away", fontSize = 13.sp, color = LuxeTextSecondary, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.width(12.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SuccessGreen.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    "Open Now",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = LuxeBorder)
                    Spacer(Modifier.height(16.dp))

                    // Description
                    Text(
                        text = "Expert plumbers for all your home and commercial needs. Fast, reliable and affordable service with professional support.",
                        fontSize = 14.sp,
                        color = LuxeTextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(20.dp))

                    // 3. Key Benefits Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        BenefitPill(Icons.Default.FlashOn, "Quick\nService")
                        BenefitPill(Icons.Default.Person, "Skilled\nProfessionals")
                        BenefitPill(Icons.Default.LocalOffer, "Affordable\nPricing")
                    }
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
            }

            // 4. Package Selection with Price
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LuxeCard)
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Select Package",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = LuxeTextPrimary,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        PackageCard("Basic", "Minor repair & inspection", 299.0, selectedPackage == "Basic", true) {
                            selectedPackage = "Basic"
                        }
                        PackageCard("Standard", "Repair + Cleaning", 499.0, selectedPackage == "Standard", false) {
                            selectedPackage = "Standard"
                        }
                        PackageCard("Premium", "Full Maintenance", 799.0, selectedPackage == "Premium", false) {
                            selectedPackage = "Premium"
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    // 5. What's Included
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "What's Included",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = LuxeTextPrimary
                        )
                        TextButton(onClick = {}) {
                            Text("View More", color = LuxeAccentSage, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    IncludedItem("Visit Charges")
                    IncludedItem("Basic Repair ($selectedPackage Service)")
                    IncludedItem("Inspection & Testing")
                }
            }
        }
    }
}

@Composable
fun BenefitPill(icon: ImageVector, label: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = LuxeHighlightChampagne.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = LuxeAccentSage, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LuxeTextPrimary, lineHeight = 14.sp)
        }
    }
}

@Composable
fun RowScope.PackageCard(
    title: String,
    subtitle: String,
    price: Double,
    selected: Boolean,
    isPopular: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .weight(1f)
            .height(150.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) LuxeHighlightChampagne.copy(alpha = 0.3f) else LuxeCard,
        border = BorderStroke(2.dp, if (selected) PkPink else LuxeBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                if (isPopular) {
                    Surface(
                        color = PkPink,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Most Popular",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                }
                Text(title, fontWeight = FontWeight.Black, fontSize = 13.sp, color = LuxeTextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 10.sp, color = LuxeTextSecondary, lineHeight = 12.sp, maxLines = 2)
            }

            Text("₹${price.toInt()}", fontWeight = FontWeight.Black, fontSize = 15.sp, color = LuxeTextPrimary)
        }
    }
}

@Composable
fun IncludedItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Check, null, tint = SuccessGreen, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 13.sp, color = LuxeTextSecondary, fontWeight = FontWeight.Medium)
    }
}
