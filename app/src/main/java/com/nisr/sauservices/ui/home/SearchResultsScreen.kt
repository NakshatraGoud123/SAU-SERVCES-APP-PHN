package com.nisr.sauservices.ui.home

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.ResidentialData
import com.nisr.sauservices.data.model.HomeEssentialsData
import com.nisr.sauservices.data.model.HomeProduct
import com.nisr.sauservices.data.model.GroceryShop
import com.nisr.sauservices.data.model.ResidentialServiceItem
import com.nisr.sauservices.ui.viewmodel.ResidentialViewModel
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.components.LuxuryCard

sealed class SearchResult {
    data class ServiceResult(val service: ResidentialServiceItem) : SearchResult()
    data class ProductResult(val product: HomeProduct, val shop: GroceryShop) : SearchResult()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    navController: NavController,
    initialQuery: String,
    residentialViewModel: ResidentialViewModel
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val serviceResults = if (searchQuery.isBlank()) emptyList() else ResidentialData.services.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.category.contains(searchQuery, ignoreCase = true)
    }.map { SearchResult.ServiceResult(it) }

    val productResults = if (searchQuery.isBlank()) emptyList() else HomeEssentialsData.products.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        it.category.contains(searchQuery, ignoreCase = true)
    }.map { product ->
        val shop = HomeEssentialsData.shops.find { it.id == product.shopId } ?: HomeEssentialsData.shops.first()
        SearchResult.ProductResult(product, shop)
    }

    val allResults = serviceResults + productResults

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LuxuryBackground)) {
                TopAppBar(
                    title = { Text("Search", fontWeight = FontWeight.Bold, color = LuxuryTextPrimary) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxuryTextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxuryBackground)
                )
                
                // Active Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .focusRequester(focusRequester),
                    placeholder = { Text("Search for milk, services...", color = LuxuryTextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = LuxuryGold) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Close, null, tint = LuxuryTextSecondary)
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LuxuryGold,
                        unfocusedBorderColor = LuxuryBorder,
                        focusedContainerColor = LuxuryCard,
                        unfocusedContainerColor = LuxuryCard
                    ),
                    singleLine = true
                )
            }
        },
        containerColor = LuxuryBackground
    ) { padding ->
        if (allResults.isEmpty() && searchQuery.isNotEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.SearchOff, null, tint = LuxuryGold.copy(alpha = 0.3f), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("No results found for \"$searchQuery\"", color = LuxuryTextSecondary)
                }
            }
        } else if (searchQuery.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Storefront, null, tint = LuxuryGold.copy(alpha = 0.1f), modifier = Modifier.size(80.dp))
                    Spacer(Modifier.height(16.dp))
                    Text("Try searching for 'Milk' or 'AC Repair'", color = LuxuryTextSecondary)
                    Text("Find local vendors near you", fontSize = 12.sp, color = LuxuryTextSecondary.copy(alpha = 0.6f))
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allResults) { result ->
                    when (result) {
                        is SearchResult.ServiceResult -> {
                            ResidentialServiceCardDesign(
                                service = result.service,
                                onAdd = { 
                                    residentialViewModel.selectService(result.service.id)
                                    navController.navigate(com.nisr.sauservices.ui.Screen.PartnerList(result.service.id))
                                }
                            )
                        }
                        is SearchResult.ProductResult -> {
                            ShopProductSearchCard(
                                product = result.product,
                                shop = result.shop,
                                onClick = {
                                    navController.navigate(com.nisr.sauservices.ui.Screen.HomeEssentialsCategory(result.product.subcategoryId, result.shop.id))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopProductSearchCard(product: HomeProduct, shop: GroceryShop, onClick: () -> Unit) {
    LuxuryCard(onClick = onClick) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Icon/Image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LuxuryGold.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, null, tint = LuxuryGold, modifier = Modifier.size(30.dp))
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = LuxuryTextPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "₹${product.price}",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = LuxuryGold
                    )
                }
                
                Spacer(Modifier.height(4.dp))
                
                // Vendor Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, null, tint = LuxuryTextSecondary, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        shop.name,
                        fontSize = 12.sp,
                        color = LuxuryTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.width(8.dp))
                    
                    // Open/Closed Status
                    Surface(
                        color = if (shop.isOpen) SuccessGreen.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (shop.isOpen) "OPEN" else "CLOSED",
                            color = if (shop.isOpen) SuccessGreen else ErrorRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    "Local vendor • ${shop.deliveryTime}",
                    fontSize = 10.sp,
                    color = LuxuryGold.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            }
            
            Icon(Icons.Default.ChevronRight, null, tint = LuxuryBorder)
        }
    }
}
