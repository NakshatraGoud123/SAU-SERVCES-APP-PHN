package com.nisr.sauservices.ui.education

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.School
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
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.theme.LightPink
import java.net.URLDecoder
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationSubCategoryScreen(navController: NavController, category: String) {
    val decodedCategory = URLDecoder.decode(category, "UTF-8")
    
    val subCategories = when (decodedCategory) {
        "Tutor" -> listOf("School Tuitions", "College Tuitions", "Competitive Exams", "Home Tuition", "Online Tuition")
        "Language" -> listOf("English", "Hindi", "French", "German", "Spoken English")
        "Music" -> listOf("Guitar", "Piano", "Singing", "Drums", "Violin")
        "Arts" -> listOf("Drawing", "Painting", "Craft", "Digital Art", "Calligraphy")
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(decodedCategory, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "Select Sub-Category",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PinkPrimary
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subCategories) { sub ->
                    EduSubCategoryCard(sub) {
                        val encoded = URLEncoder.encode(sub, "UTF-8")
                        navController.navigate(Screen.EducationCourses(encoded))
                    }
                }
            }
        }
    }
}

@Composable
fun EduSubCategoryCard(name: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightPink),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.School, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(30.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 16.sp
            )
        }
    }
}
