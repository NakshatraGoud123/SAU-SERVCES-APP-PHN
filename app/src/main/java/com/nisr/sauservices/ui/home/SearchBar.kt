package com.nisr.sauservices.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarUI(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    
    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = "Search services...",
                color = LuxuryTextSecondary,
                fontSize = 15.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = LuxuryGold
            )
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = LuxuryGold,
            unfocusedBorderColor = LuxuryBorder,
            focusedContainerColor = LuxuryCard,
            unfocusedContainerColor = LuxuryCard,
            cursorColor = LuxuryGold,
            focusedTextColor = LuxuryTextPrimary,
            unfocusedTextColor = LuxuryTextPrimary,
            focusedPlaceholderColor = LuxuryTextSecondary,
            unfocusedPlaceholderColor = LuxuryTextSecondary
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                if (searchQuery.isNotBlank()) {
                    navController.navigate("search_results/$searchQuery")
                }
            }
        )
    )
}
