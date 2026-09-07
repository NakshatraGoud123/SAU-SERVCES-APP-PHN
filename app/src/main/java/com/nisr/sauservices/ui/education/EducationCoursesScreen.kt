package com.nisr.sauservices.ui.education

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.nisr.sauservices.ui.viewmodel.EducationCartViewModel
import java.net.URLDecoder

private val EduPrimary = Color(0xFF1565C0)
private val EduAccent = Color(0xFFE3F2FD)

data class EduCourse(
    val id: String,
    val name: String,
    val description: String,
    val duration: String,
    val price: Int,
    val category: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EducationCoursesScreen(navController: NavController, subcategory: String, viewModel: EducationCartViewModel) {
    val decodedSub = URLDecoder.decode(subcategory, "UTF-8")
    
    val courses = when (decodedSub) {
        "School Tuitions" -> listOf(
            EduCourse("s1", "Class 1–5 All Subjects", "Fundamental coaching for primary kids", "1 Month", 1500, "Tutor"),
            EduCourse("s2", "Class 6–10 Maths & Science", "Focused learning for high school", "1 Month", 2500, "Tutor")
        )
        "Competitive Exams" -> listOf(
            EduCourse("c1", "IIT-JEE Foundation", "Early prep for engineering entrance", "1 Month", 4000, "Tutor"),
            EduCourse("c2", "NEET Crash Course", "Intensive medical entrance prep", "1 Month", 4500, "Tutor")
        )
        "Guitar" -> listOf(
            EduCourse("g1", "Beginner Guitar Course", "Learn basic chords and rhythms", "1 Month", 2000, "Music"),
            EduCourse("g2", "Advanced Guitar Training", "Solo techniques and music theory", "1 Month", 3500, "Music")
        )
        "Painting" -> listOf(
            EduCourse("p1", "Basic Painting Course", "Watercolor and sketching basics", "1 Month", 1200, "Arts"),
            EduCourse("p2", "Professional Canvas Painting", "Oil painting and canvas work", "1 Month", 3000, "Arts")
        )
        else -> listOf(
            EduCourse("d1", "Standard $decodedSub", "Regular session for $decodedSub", "1 Month", 1000, "General")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(decodedSub, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.EducationCart) }) {
                        BadgedBox(badge = {
                            if (viewModel.cartItems.isNotEmpty()) {
                                Badge(containerColor = EduPrimary) {
                                    Text(viewModel.cartItems.sumOf { it.quantity }.toString(), color = Color.White)
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(courses) { course ->
                EduCourseCard(course) {
                    viewModel.addItem(course.id, course.name, course.price, course.category, course.duration)
                }
            }
        }
    }
}

@Composable
fun EduCourseCard(course: EduCourse, onAdd: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EduAccent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Book, contentDescription = null, tint = EduPrimary, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = course.name, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color.Black)
                    Text(text = course.description, fontSize = 13.sp, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "₹${course.price}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = EduPrimary)
                    Text(text = course.duration, fontSize = 12.sp, color = Color.Gray)
                }
                
                Button(
                    onClick = onAdd,
                    colors = ButtonDefaults.buttonColors(containerColor = EduPrimary),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add to Cart", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
