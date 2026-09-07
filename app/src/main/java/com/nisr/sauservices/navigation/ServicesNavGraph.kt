package com.nisr.sauservices.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nisr.sauservices.ui.business.*
import com.nisr.sauservices.ui.education.*
import com.nisr.sauservices.ui.healthcare.*
import com.nisr.sauservices.ui.home.*
import com.nisr.sauservices.ui.lifestyle.*
import com.nisr.sauservices.ui.essentials.*
import com.nisr.sauservices.ui.mens.*
import com.nisr.sauservices.ui.tech.*
import com.nisr.sauservices.ui.viewmodel.*
import com.nisr.sauservices.ui.womens.*
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.partner.PartnerListScreen
import com.nisr.sauservices.ui.partner.PartnerProfileScreen
import com.nisr.sauservices.ui.ReviewsScreen

fun NavGraphBuilder.servicesNavGraph(
    navController: NavController,
    residentialViewModel: ResidentialViewModel,
    businessViewModel: BusinessViewModel,
    lifestyleViewModel: LifestyleViewModel,
    techViewModel: TechServicesViewModel,
    mensGroomingViewModel: MensGroomingViewModel,
    womensBeautyViewModel: WomensBeautyViewModel,
    healthViewModel: HealthcareViewModel,
    homeCartViewModel: CartViewModel,
    educationCartViewModel: EducationCartViewModel
) {
    // --- RESIDENTIAL SERVICES ---
    composable<Screen.ResidentialCategories> {
        ResidentialCategoryScreen(navController)
    }
    composable<Screen.ResidentialSubcategories> { backStackEntry ->
        val route: Screen.ResidentialSubcategories = backStackEntry.toRoute()
        ResidentialSubcategoryScreen(navController, route.categoryId)
    }
    composable<Screen.ResidentialServices> { backStackEntry ->
        val route: Screen.ResidentialServices = backStackEntry.toRoute()
        ResidentialServiceListScreen(navController, route.categoryId, route.subcategoryId, residentialViewModel, homeCartViewModel)
    }

    // --- BUSINESS SERVICES ---
    composable<Screen.BusinessMain> {
        BusinessMainScreen(navController)
    }

    composable<Screen.BusinessSubcategories> { backStackEntry ->
        val route: Screen.BusinessSubcategories = backStackEntry.toRoute()
        BusinessSubCategoryScreen(navController, route.category)
    }
    composable<Screen.BusinessServices> { backStackEntry ->
        val route: Screen.BusinessServices = backStackEntry.toRoute()
        BusinessServicesScreen(navController, route.subcategory, businessViewModel)
    }

    // --- LIFESTYLE SERVICES ---
    composable<Screen.LifestyleSubcategories> { backStackEntry ->
        val route: Screen.LifestyleSubcategories = backStackEntry.toRoute()
        LifestyleSubCategoryScreen(navController, route.category)
    }
    composable<Screen.LifestyleServices> { backStackEntry ->
        val route: Screen.LifestyleServices = backStackEntry.toRoute()
        LifestyleServicesScreen(navController, route.subcategory, lifestyleViewModel)
    }

    // --- TECH SERVICES ---
    composable<Screen.TechSubcategories> { backStackEntry ->
        val route: Screen.TechSubcategories = backStackEntry.toRoute()
        TechSubCategoryScreen(navController, route.category)
    }
    composable<Screen.TechServices> { backStackEntry ->
        val route: Screen.TechServices = backStackEntry.toRoute()
        TechServiceListScreen(navController, route.subcategory, techViewModel)
    }

    // --- MENS GROOMING ---
    composable<Screen.MensSubcategories> { backStackEntry ->
        val route: Screen.MensSubcategories = backStackEntry.toRoute()
        MensSubcategoryScreen(navController, route.category)
    }
    composable<Screen.MensServices> { backStackEntry ->
        val route: Screen.MensServices = backStackEntry.toRoute()
        MensServiceListScreen(navController, route.subcategory, mensGroomingViewModel)
    }

    // --- WOMENS BEAUTY ---
    composable<Screen.WomensBeautySubcategories> { backStackEntry ->
        val route: Screen.WomensBeautySubcategories = backStackEntry.toRoute()
        WomensBeautySubcategoryScreen(navController, route.category)
    }
    composable<Screen.WomensBeautyServices> { backStackEntry ->
        val route: Screen.WomensBeautyServices = backStackEntry.toRoute()
        BeautyServiceListScreen(navController, route.subcategory, womensBeautyViewModel)
    }

    // --- HEALTHCARE ---
    composable<Screen.HealthcareMain> {
        HealthcareCategoryScreen(navController)
    }
    composable<Screen.HealthcareSubcategories> { backStackEntry ->
        val route: Screen.HealthcareSubcategories = backStackEntry.toRoute()
        HealthcareSubcategoryScreen(navController, route.category)
    }
    composable<Screen.HealthcareServices> { backStackEntry ->
        val route: Screen.HealthcareServices = backStackEntry.toRoute()
        HealthcareServiceListScreen(navController, route.subcategory, healthViewModel)
    }

    // --- EDUCATION ---
    composable<Screen.EducationSubcategories> { backStackEntry ->
        val route: Screen.EducationSubcategories = backStackEntry.toRoute()
        EducationSubCategoryScreen(navController, route.category)
    }
    composable<Screen.EducationCourses> { backStackEntry ->
        val route: Screen.EducationCourses = backStackEntry.toRoute()
        EducationCoursesScreen(navController, route.subcategory, educationCartViewModel)
    }
    composable<Screen.EducationCart> { 
        EducationCartScreen(navController, educationCartViewModel) 
    }
    composable<Screen.EducationSuccess> { 
        EducationSuccessScreen(navController) 
    }

    composable<Screen.HomeEssentialsMain> {
        HomeEssentialsMainScreen(navController, homeCartViewModel)
    }
    composable<Screen.HomeEssentialsCategory> { backStackEntry ->
        val route: Screen.HomeEssentialsCategory = backStackEntry.toRoute()
        HomeEssentialsCategoryScreen(navController, route.categoryId, homeCartViewModel)
    }
    composable<Screen.HomeEssentialsItems> { backStackEntry ->
        val route: Screen.HomeEssentialsItems = backStackEntry.toRoute()
        // Map to same screen or implement sub-filter if needed
        HomeEssentialsCategoryScreen(navController, route.subcategoryId, homeCartViewModel)
    }

    // Partner Flow
    composable<Screen.PartnerList> { backStackEntry ->
        val route: Screen.PartnerList = backStackEntry.toRoute()
        PartnerListScreen(navController, route.serviceId, residentialViewModel)
    }

    composable<Screen.PartnerProfile> { backStackEntry ->
        val route: Screen.PartnerProfile = backStackEntry.toRoute()
        PartnerProfileScreen(
            navController = navController,
            partnerId = route.partnerId,
            serviceId = route.serviceId,
            viewModel = residentialViewModel
        )
    }

    composable<Screen.Reviews> { backStackEntry ->
        val route: Screen.Reviews = backStackEntry.toRoute()
        ReviewsScreen(navController, route.partnerId)
    }
}
