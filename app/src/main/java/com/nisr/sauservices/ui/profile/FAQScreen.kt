package com.nisr.sauservices.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(navController: NavController) {
    val faqs = emptyList<FAQItem>()

    var expandedIndex by remember { mutableIntStateOf(-1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Frequently Asked", fontWeight = FontWeight.Black, color = LuxeTextPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = LuxeTextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LuxeBackground)
            )
        },
        containerColor = LuxeBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(faqs) { index, faq ->
                FAQCard(
                    faq = faq,
                    isExpanded = expandedIndex == index,
                    onToggle = {
                        expandedIndex = if (expandedIndex == index) -1 else index
                    }
                )
            }
        }
    }
}

@Composable
fun FAQCard(faq: FAQItem, isExpanded: Boolean, onToggle: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = LuxeCard,
        border = BorderStroke(1.dp, if (isExpanded) LuxeAccentSage else LuxeBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = faq.question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                    color = LuxeTextPrimary
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = if (isExpanded) LuxeAccentSage else LuxeTextSecondary
                )
            }
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 14.sp,
                        color = LuxeTextSecondary,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

data class FAQItem(val question: String, val answer: String)
