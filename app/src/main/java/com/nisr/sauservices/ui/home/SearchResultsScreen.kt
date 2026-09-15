package com.nisr.sauservices.ui.home

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.nisr.sauservices.data.model.*
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.*
import com.nisr.sauservices.ui.viewmodel.SearchUiState
import com.nisr.sauservices.ui.viewmodel.SearchViewModel

// ============================================================
// LUXE BRAND COLORS (Local for precision)
// ============================================================
private val LuxeBackground = Color(0xFFFDFBFA)
private val LuxeCard = Color(0xFFFFFFFF)
private val LuxeTextPrimary = Color(0xFF423F3D)
private val LuxeTextSecondary = Color(0xFF8D7F77)
private val LuxeAccentSage = Color(0xFF96A68F)
private val LuxeHighlightChampagne = Color(0xFFF5E6D3)
private val LuxeBorder = Color(0xFFEFE9E4)
private val LuxeGold = Color(0xFFE8C66A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    navController: NavController,
    initialQuery: String,
    viewModel: SearchViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf(initialQuery) }
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                val query = data?.get(0) ?: ""
                if (query.isNotEmpty()) {
                    searchQuery = query
                    viewModel.performSearch(query)
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        if (initialQuery.isNotEmpty()) {
            viewModel.performSearch(initialQuery)
        }
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(LuxeBackground)) {
                TopAppBar(
                    title = { Text("Search", fontWeight = FontWeight.Black, color = LuxeTextPrimary, fontFamily = FontFamily.Serif) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
                )
                
                // Active Search Input
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = LuxeTextSecondary.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp),
                    color = LuxeCard,
                    border = BorderStroke(1.dp, LuxeBorder)
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { 
                            searchQuery = it
                            viewModel.performSearch(it)
                        },
                        modifier = Modifier.fillMaxSize().focusRequester(focusRequester),
                        placeholder = { Text("What can we find for you?", color = LuxeTextSecondary, fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = LuxeAccentSage) },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { 
                                        searchQuery = "" 
                                        viewModel.performSearch("")
                                    }) {
                                        Icon(Icons.Default.Close, null, tint = LuxeTextSecondary, modifier = Modifier.size(18.dp))
                                    }
                                }
                                IconButton(
                                    onClick = {
                                        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                            putExtra(RecognizerIntent.EXTRA_PROMPT, "What can SAU find for you?")
                                        }
                                        voiceLauncher.launch(intent)
                                    }
                                ) {
                                    Icon(Icons.Default.Mic, null, tint = LuxeAccentSage, modifier = Modifier.size(20.dp))
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = LuxeAccentSage
                        ),
                        singleLine = true
                    )
                }
            }
        },
        containerColor = LuxeBackground
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is SearchUiState.Idle -> PopularSearchSection { 
                    searchQuery = it
                    viewModel.performSearch(it)
                }
                is SearchUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LuxeAccentSage)
                }
                is SearchUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WifiOff, null, tint = LuxeAccentSage, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(16.dp))
                        Text("Search timed out", color = LuxeTextPrimary, fontWeight = FontWeight.Bold)
                        Text("Please check your internet connection", color = LuxeTextSecondary, fontSize = 14.sp)
                        TextButton(onClick = { viewModel.performSearch(searchQuery) }) {
                            Text("Retry", color = LuxeAccentSage)
                        }
                    }
                }
                is SearchUiState.Success -> {
                    if (state.vendors.isEmpty() && state.products.isEmpty() && state.services.isEmpty()) {
                        SearchEmptyState(Icons.Default.SearchOff, "No matches found for \"$searchQuery\"")
                    } else {
                        SearchContent(state, navController)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchContent(state: SearchUiState.Success, navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (state.vendors.isNotEmpty()) {
            item { LuxeSectionHeader("SHOPS") }
            items(state.vendors) { vendor ->
                LuxeWideVendorCard(vendor) {
                    navController.navigate(Screen.MerchantShop(vendor.id))
                }
            }
        }

        if (state.products.isNotEmpty()) {
            item { Spacer(Modifier.height(12.dp)); LuxeSectionHeader("PRODUCTS") }
            items(state.products) { product ->
                LuxeSearchItemRow(
                    title = product.name,
                    subtitle = product.unit,
                    price = "₹${product.price.toInt()}",
                    imageUrl = product.imageUrl,
                    onClick = { navController.navigate(Screen.MerchantShop(product.vendorId ?: "")) }
                )
            }
        }

        if (state.services.isNotEmpty()) {
            item { Spacer(Modifier.height(12.dp)); LuxeSectionHeader("SERVICES") }
            items(state.services) { service ->
                LuxeSearchItemRow(
                    title = service.name,
                    subtitle = "Professional Service",
                    price = "₹${service.price.toInt()}",
                    imageUrl = service.imageUrl,
                    onClick = { navController.navigate(Screen.PartnerList(service.id)) }
                )
            }
        }
        
        item { Spacer(Modifier.height(32.dp)) }
    }
}

@Composable
private fun LuxeSearchItemRow(title: String, subtitle: String, price: String, imageUrl: String?, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, LuxeBorder)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(70.dp),
                shape = RoundedCornerShape(12.dp),
                color = LuxeHighlightChampagne.copy(alpha = 0.4f)
            ) {
                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(imageUrl).crossfade(true).build(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Inventory2, null, modifier = Modifier.padding(20.dp), tint = LuxeAccentSage)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = LuxeTextPrimary, maxLines = 1)
                Text(subtitle, fontSize = 12.sp, color = LuxeTextSecondary)
                Text(price, fontWeight = FontWeight.Black, color = LuxeGold, fontSize = 14.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = LuxeBorder, modifier = Modifier.size(20.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PopularSearchSection(onSelect: (String) -> Unit) {
    val suggestions = listOf("Milk", "Biryani", "Electrician", "Pizza", "Plumber", "Fresh Fruits")
    
    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(
            "POPULAR SEARCHES",
            style = MaterialTheme.typography.labelSmall,
            color = LuxeAccentSage,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.5.sp
        )
        
        Spacer(Modifier.height(16.dp))
        
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            suggestions.forEach { tag ->
                SuggestionChip(
                    onClick = { onSelect(tag) },
                    label = { Text(tag) },
                    shape = RoundedCornerShape(12.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = LuxeHighlightChampagne.copy(alpha = 0.4f),
                        labelColor = LuxeTextPrimary
                    ),
                    border = BorderStroke(1.dp, LuxeBorder)
                )
            }
        }
        
        Spacer(Modifier.height(48.dp))
        
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Storefront, null, tint = LuxeHighlightChampagne, modifier = Modifier.size(64.dp))
                Spacer(Modifier.height(12.dp))
                Text("Search our entire luxury ecosystem", color = LuxeTextSecondary, fontSize = 13.sp)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    content: @Composable FlowRowScope.() -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        content = content
    )
}

@Composable
private fun SearchEmptyState(icon: ImageVector, message: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(32.dp),
            color = LuxeHighlightChampagne.copy(alpha = 0.3f),
            border = BorderStroke(1.dp, LuxeBorder)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, null, modifier = Modifier.size(40.dp), tint = LuxeAccentSage)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(message, color = LuxeTextSecondary, textAlign = TextAlign.Center, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun LuxeSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = LuxeTextSecondary,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}
