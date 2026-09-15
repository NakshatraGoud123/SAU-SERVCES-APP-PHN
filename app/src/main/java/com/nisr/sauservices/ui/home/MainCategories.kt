package com.nisr.sauservices.ui.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
    val route: Any,
    val icon: ImageVector? = null,
    @DrawableRes val imageRes: Int? = null
)

@Composable
fun CategoriesGrid(
    navController: NavController, 
    showAll: Boolean = false
) {
    val allCategories = listOf(
        CategoryItem("Kirana", Screen.CategoryVendors("Kirana"), icon = Icons.Default.Storefront),
        CategoryItem("Meat Shop", Screen.CategoryVendors("Meat"), icon = Icons.Default.Restaurant),
        CategoryItem("Book Shop", Screen.CategoryVendors("Books"), icon = Icons.AutoMirrored.Filled.MenuBook),
        CategoryItem("Medical", Screen.CategoryVendors("Medical"), icon = Icons.Default.MedicalServices),
        CategoryItem("Vegetables", Screen.CategoryVendors("Vegetables"), icon = Icons.Default.Eco),
        CategoryItem("Bakery", Screen.CategoryVendors("Bakery"), icon = Icons.Default.BakeryDining),
        CategoryItem("Chinese", Screen.CategoryVendors("Chinese"), icon = Icons.Default.Fastfood),
        CategoryItem("Dry Fruits", Screen.CategoryVendors("Dry Fruits"), icon = Icons.Default.ShoppingBasket)
    )

    Column(modifier = Modifier.padding(top = 8.dp)) {
        val rows = allCategories.chunked(3) 
        
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowItems.forEach { item ->
                    CategoryBoutiqueCard(
                        item = item, 
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(item.route)
                        }
                    )
                }
                if (rowItems.size < 3) {
                    repeat(3 - rowItems.size) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryBoutiqueCard(item: CategoryItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, LuxeBorder),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(LuxeHighlightChampagne.copy(alpha = 0.6f)),
                contentAlignment = Alignment.Center
            ) {
                if (item.imageRes != null) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.name,
                        modifier = Modifier.size(30.dp),
                        contentScale = ContentScale.Fit
                    )
                } else if (item.icon != null) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.name,
                        tint = LuxeAccentSage,
                        modifier = Modifier.size(28.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Rounded.Apps,
                        contentDescription = item.name,
                        tint = LuxeAccentSage,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                color = LuxeTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = 0.5.sp
            )
        }
    }
}
