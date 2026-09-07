package com.nisr.sauservices.ui.partner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.ResidentialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerProfileScreen(
    navController: NavController, 
    partnerId: String,
    serviceId: String,
    viewModel: ResidentialViewModel
) {
    val partner = PartnerData.getPartnerById(partnerId)

    if (partner == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Partner not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Partner Profile", fontWeight = FontWeight.Bold, color = LuxuryTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 16.dp,
                color = LuxuryBackground
            ) {
                Button(
                    onClick = {
                        partner?.let { viewModel.selectPartner(it.id) }
                        viewModel.selectService(serviceId)
                        navController.navigate(
                            Screen.ResidentialBookingDetails(
                                partnerId = partnerId,
                                serviceId = serviceId
                            )
                        )
                    },
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LuxuryGold)
                ) {
                    Text("Book Now", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = LuxuryBackground)
                }
            }
        },
        containerColor = LuxuryBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = LuxuryCard
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(LuxuryGold.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = LuxuryGold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = partner.name,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryTextPrimary
                        )
                        if (partner.isVerified) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.padding(start = 8.dp).size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "AC Repair Expert",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = LuxuryTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Stats Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LuxuryCard)
                    .border(1.dp, LuxuryBorder, RoundedCornerShape(16.dp))
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Rating", value = "⭐ ${partner.rating}")
                StatItem(label = "Jobs", value = "${partner.completedJobs}+")
                StatItem(label = "Exp.", value = "${partner.experienceYears} Years")
            }

            // Details Section
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                ProfileSection(title = "About", content = partner.about)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LuxuryCard),
                    border = BorderStroke(1.dp, LuxuryBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Additional Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = LuxuryTextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoRow(icon = Icons.Default.History, label = "Member since", value = partner.memberSince)
                        InfoRow(icon = Icons.Default.LocationOn, label = "Distance", value = "${partner.distanceKm} km away")
                        InfoRow(icon = Icons.Default.CurrencyRupee, label = "Starting from", value = "₹${partner.startingPrice.toInt()}")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = LuxuryTextPrimary)
        Text(text = label, fontSize = 14.sp, color = LuxuryTextSecondary)
    }
}

@Composable
fun ProfileSection(title: String, content: String) {
    Column {
        Text(text = title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = LuxuryTextPrimary)
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = content,
            fontSize = 15.sp,
            color = LuxuryTextSecondary,
            lineHeight = 22.sp
        )
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = LuxuryGold)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "$label:", fontSize = 15.sp, color = LuxuryTextSecondary)
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = LuxuryTextPrimary)
    }
}
