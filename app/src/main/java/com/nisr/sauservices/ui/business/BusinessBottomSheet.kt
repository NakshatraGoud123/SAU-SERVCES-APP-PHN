package com.nisr.sauservices.ui.business

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.LocalPostOffice
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.LightPink
import com.nisr.sauservices.ui.theme.PinkPrimary
import java.net.URLEncoder

data class BusinessSheetCategory(
    val name: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessBottomSheet(
    navController: NavController,
    onDismiss: () -> Unit
) {

    val categories = listOf(
        BusinessSheetCategory(
            name = "IT Support",
            icon = Icons.Default.Computer
        ),
        BusinessSheetCategory(
            name = "Marketing",
            icon = Icons.Default.Campaign
        ),
        BusinessSheetCategory(
            name = "Accounting",
            icon = Icons.Default.AccountBalance
        ),
        BusinessSheetCategory(
            name = "Legal",
            icon = Icons.Default.Gavel
        ),
        BusinessSheetCategory(
            name = "Printing",
            icon = Icons.Default.Print
        ),
        BusinessSheetCategory(
            name = "Courier",
            icon = Icons.Default.LocalPostOffice
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(
            topStart = 24.dp,
            topEnd = 24.dp
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {

            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 8.dp
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Business Services",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PinkPrimary
                )

                IconButton(
                    onClick = onDismiss
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close"
                    )
                }
            }

            // Business categories
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                items(categories) { category ->

                    BusinessCategoryItem(
                        category = category,
                        onClick = {

                            // Close bottom sheet
                            onDismiss()

                            // Navigate to Business Sub Category screen
                            val encoded = URLEncoder.encode(category.name, "UTF-8")
                            navController.navigate(
                                Screen.BusinessSubcategories(encoded)
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BusinessCategoryItem(
    category: BusinessSheetCategory,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(LightPink),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = PinkPrimary,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = category.name,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}