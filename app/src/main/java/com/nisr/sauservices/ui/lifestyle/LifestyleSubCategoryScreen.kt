package com.nisr.sauservices.ui.lifestyle

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import java.net.URLDecoder
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifestyleSubCategoryScreen(navController: NavController, category: String) {
    val decodedCategory = URLDecoder.decode(category, "UTF-8")
    
    val subCategories = when (decodedCategory) {
        "Event Planner" -> listOf("Birthday Events", "Wedding Events", "Corporate Events", "Private Events")
        "Photographer" -> listOf("Photography", "Videography", "Editing Services", "Drone")
        "Personal Trainer" -> listOf("Fitness Training", "Yoga", "Diet Plans", "Home Personal Trainer")
        "Travel Agent" -> listOf("Domestic Tours", "International Tours", "Ticket Booking", "Hotel Booking")
        "Pet Care" -> listOf("Pet Grooming", "Pet Walking", "Pet Sitting", "Vet Services")
        "Gardening" -> listOf("Garden Setup", "Maintenance", "Plant Supply", "Lawn Services")
        else -> emptyList()
    }

    LuxuryScaffold(
        title = decodedCategory,
        onBackClick = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "Select Premium Service",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = LuxuryGold,
                fontFamily = FontFamily.Serif
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subCategories) { sub ->
                    LuxuryCard(
                        onClick = {
                            val encoded = URLEncoder.encode(sub, "UTF-8")
                            navController.navigate(Screen.LifestyleServices(encoded))
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth()
                                .heightIn(min = 60.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sub,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = LuxuryTextPrimary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
