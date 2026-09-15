package com.nisr.sauservices.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.R
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*

data class CategoryItem(
    val name: String, 
    val route: Any = "",
    @DrawableRes val imageRes: Int? = null
)

@Composable
fun CategoriesGrid(
    navController: NavController, 
    showAll: Boolean = false
) {
    val allCategories = listOf(
        CategoryItem("Groceries", Screen.HomeEssentialsMain, R.drawable.home_essentials),
        CategoryItem("Food", Screen.FoodCategories, R.drawable.home_lifestyle),
        CategoryItem("Services", Screen.ResidentialCategories, R.drawable.residential_services),
        CategoryItem("Pharmacy", Screen.HealthcareMain, R.drawable.healthcare_pharmacy),
        CategoryItem("More", Screen.Categories, null)
    )

    val displayList = allCategories

    Column(modifier = Modifier.padding(top = 8.dp)) {
        val rows = displayList.chunked(5)
        
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { item ->
                    CategoryCard(
                        item = item, 
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(item.route)
                        }
                    )
                }
                if (rowItems.size < 5) {
                    repeat(5 - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(LuxuryCard)
                .border(1.dp, LuxuryBorder, RoundedCornerShape(16.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            if (item.imageRes != null) {
                Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Rounded.Apps,
                    contentDescription = item.name,
                    tint = LuxuryGold,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        Spacer(Modifier.height(8.dp))
        
        Text(
            text = item.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = LuxuryTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
