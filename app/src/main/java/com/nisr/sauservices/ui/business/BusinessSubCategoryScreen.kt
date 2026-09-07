package com.nisr.sauservices.ui.business

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.text.font.FontFamily
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import java.net.URLDecoder
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessSubCategoryScreen(navController: NavController, category: String) {
    val decodedCategory = URLDecoder.decode(category, "UTF-8")
    
    val subCategories = when (decodedCategory) {
        "IT Support" -> listOf("Computer Repair", "Network Setup", "Software Services", "AMC Services")
        "Marketing" -> listOf("Digital Marketing", "Design Services", "Branding", "Video Promotion")
        "Accounting" -> listOf("Bookkeeping", "GST Services", "Tax Services", "Payroll")
        "Legal" -> listOf("Business Registration", "Legal Drafting", "Consultation", "Documentation")
        "Printing" -> listOf("Office Printing", "Marketing Prints", "Custom Prints", "Binding")
        "Courier" -> listOf("Local Delivery", "Domestic Courier", "International Courier", "Business Shipping")
        else -> emptyList()
    }

    LuxuryScaffold(
        title = decodedCategory,
        onBackClick = { navController.popBackStack() }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "Select Service",
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
                            navController.navigate(Screen.BusinessServices(encoded))
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
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
