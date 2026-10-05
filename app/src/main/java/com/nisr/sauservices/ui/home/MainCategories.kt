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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nisr.sauservices.R
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

data class CategoryItem(
    val name: String, 
    val route: Any,
    val icon: ImageVector? = null,
    @DrawableRes val imageRes: Int? = null,
    val imageUrl: String = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=600&auto=format&fit=crop&q=80"
)

@Composable
fun CategoriesGrid(
    navController: NavController, 
    showAll: Boolean = false
) {
    val allCategories = listOf(
        CategoryItem("Essentials", Screen.HomeEssentialsMain, imageUrl = "https://images.unsplash.com/photo-1542838132-92c53300491e?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Home Services", Screen.ResidentialCategories, imageUrl = "https://images.unsplash.com/photo-1581578731548-c64695cc6952?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Mechanic", Screen.MechanicSubcategories("Mechanic Services"), imageUrl = "https://images.unsplash.com/photo-1486006920555-c77dce18193b?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Mobility", Screen.MobilityMain, imageUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Tech Repair", Screen.TechSubcategories("Mobile Repair"), imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Education", Screen.EducationSubcategories("Music & Art"), imageUrl = "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Healthcare", Screen.HealthcareMain, imageUrl = "https://images.unsplash.com/photo-1584515979956-d9f6e5d09982?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Kirana", Screen.CategoryVendors("Kirana"), imageUrl = "https://images.unsplash.com/photo-1578916171728-46686eac8d58?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Dining", Screen.CategoryVendors("Dining"), imageUrl = "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Meat Shop", Screen.CategoryVendors("Meat"), imageUrl = "https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Medical", Screen.CategoryVendors("Medical"), imageUrl = "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=600&auto=format&fit=crop&q=80"),
        CategoryItem("Bakery", Screen.CategoryVendors("Bakery"), imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?w=600&auto=format&fit=crop&q=80")
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
            .height(145.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, LuxeBorder),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                border = BorderStroke(1.5.dp, LuxeBorder),
                color = LuxeHighlightChampagne
            ) {
                if (item.imageRes != null) {
                    Image(
                        painter = painterResource(id = item.imageRes),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(item.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = item.name,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            
            Spacer(Modifier.height(10.dp))
            
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
