package com.nisr.sauservices.ui.partner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.ResidentialViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerListScreen(
    navController: NavController,
    serviceId: String,
    viewModel: ResidentialViewModel
) {
    val partners = PartnerData.getPartnersForService(serviceId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Available Partners",
                        fontWeight = FontWeight.Bold,
                        color = LuxuryTextPrimary
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LuxuryTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LuxuryBackground
                )
            )
        },
        containerColor = LuxuryBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "${partners.size} partners available near you",
                    fontSize = 15.sp,
                    color = LuxuryTextSecondary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                )
            }

            items(partners) { partner ->
                PartnerCard(
                    partner = partner,
                    onClick = {
                        viewModel.selectPartner(partner.id)
                        viewModel.selectService(serviceId)
                        navController.navigate(
                            Screen.PartnerProfile(
                                partnerId = partner.id,
                                serviceId = serviceId
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun PartnerCard(
    partner: Partner,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = LuxuryCard
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        border = BorderStroke(1.dp, LuxuryBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(
                            LuxuryGold.copy(alpha = 0.10f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = partner.name.first().uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuxuryGold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = partner.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = LuxuryTextPrimary
                        )

                        if (partner.isVerified) {
                            Spacer(modifier = Modifier.width(5.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(19.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "${partner.experienceYears} years experience",
                        fontSize = 14.sp,
                        color = LuxuryTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = LuxuryBorder)
            Spacer(modifier = Modifier.height(14.dp))

            // Rating + Jobs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⭐ ${partner.rating}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = LuxuryGold
                )

                Text(
                    text = "${partner.completedJobs}+ jobs",
                    fontSize = 14.sp,
                    color = LuxuryTextSecondary
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = LuxuryTextSecondary,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${partner.distanceKm} km",
                        fontSize = 14.sp,
                        color = LuxuryTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Starting from",
                        fontSize = 13.sp,
                        color = LuxuryTextSecondary
                    )
                    Text(
                        text = "₹${partner.startingPrice.toInt()}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LuxuryGold
                    )
                }

                Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LuxuryGold
                    )
                ) {
                    Text(
                        text = "View Profile",
                        fontWeight = FontWeight.Bold,
                        color = LuxuryBackground
                    )
                }
            }
        }
    }
}
