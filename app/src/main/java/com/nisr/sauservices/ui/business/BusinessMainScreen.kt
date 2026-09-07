package com.nisr.sauservices.ui.business

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.components.*
import com.nisr.sauservices.ui.theme.*
import java.net.URLEncoder

// Renamed to avoid collision with BusinessBottomSheet.kt
data class BusinessMainCategory(
    val name: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessMainScreen(
    navController: NavController
) {

    val categories = listOf(
        BusinessMainCategory(
            "IT Support",
            "Computer, software and network services"
        ),
        BusinessMainCategory(
            "Marketing",
            "Digital marketing, design and promotion"
        ),
        BusinessMainCategory(
            "Accounting",
            "Bookkeeping, GST, tax and payroll"
        ),
        BusinessMainCategory(
            "Legal",
            "Business registration and legal services"
        ),
        BusinessMainCategory(
            "Printing",
            "Printing, binding and custom prints"
        ),
        BusinessMainCategory(
            "Courier",
            "Local, domestic and business delivery"
        )
    )

    LuxuryScaffold(
        title = "Business Services",
        onBackClick = { navController.popBackStack() }
    ) { padding ->

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(categories) { category ->

                LuxuryCard(
                    onClick = {
                        val encodedCategory =
                            URLEncoder.encode(
                                category.name,
                                "UTF-8"
                            )

                        navController.navigate(
                            Screen.BusinessSubcategories(
                                encodedCategory
                            )
                        )
                    }
                ) {

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {

                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = LuxuryTextPrimary
                        )

                        Text(
                            text = category.description,
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = LuxuryTextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "EXPLORE →",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Black,
                            color = LuxuryGold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}
