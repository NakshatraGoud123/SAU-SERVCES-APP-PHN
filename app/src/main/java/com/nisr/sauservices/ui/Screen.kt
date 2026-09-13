package com.nisr.sauservices.ui

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable data class Splash(val id: String = "splash") : Screen()
    
    // Auth
    @Serializable data object Onboarding : Screen()
    @Serializable data object RoleSelection : Screen()
    @Serializable data class AuthOptions(val role: String) : Screen()
    @Serializable data class Login(val role: String = "customer") : Screen()
    @Serializable data class Register(val role: String = "customer") : Screen()
    @Serializable data object ForgotPassword : Screen()
    @Serializable data class ResetPassword(val email: String) : Screen()
    
    @Serializable data object Home : Screen()
    @Serializable data object Search : Screen()
    @Serializable data object Categories : Screen()
    @Serializable data class SearchResults(val query: String) : Screen()
    @Serializable data class MerchantShop(val vendorId: String) : Screen()
    @Serializable data object UniversalCheckout : Screen()
    @Serializable data class Chat(val orderId: String, val receiverId: String, val receiverName: String) : Screen()
    @Serializable data object Wallet : Screen()
    @Serializable data class CategoryVendors(val category: String) : Screen()

    // Property & Lifestyle Services (PLS)
    @Serializable data object PLSMain : Screen()
    @Serializable data class PLSSubcategories(val category: String) : Screen()
    @Serializable data class PLSServices(val subcategory: String) : Screen()
    @Serializable data class PLSBooking(val serviceId: String) : Screen()
    @Serializable data object PLSCheckout : Screen()
    @Serializable data object PLSSuccess : Screen()

    @Serializable data class ServiceList(val categoryId: String, val subcategoryId: String) : Screen()
    
    // Residential
    @Serializable data object ResidentialCategories : Screen()
    @Serializable data class ResidentialSubcategories(val categoryId: String) : Screen()
    @Serializable data class ResidentialServices(val categoryId: String, val subcategoryId: String) : Screen()
    
    @Serializable data class PartnerList(val serviceId: String) : Screen()
    @Serializable data class PartnerProfile(val partnerId: String, val serviceId: String) : Screen()
    @Serializable data class Booking(val partnerId: String, val serviceId: String) : Screen()
    @Serializable data class BookingConfirmation(val bookingId: String) : Screen()
    
    @Serializable data object MyBookings : Screen()
    @Serializable data class BookingDetails(val bookingId: String) : Screen()
    @Serializable data class Reviews(val partnerId: String) : Screen()
    
    @Serializable data object Notifications : Screen()
    @Serializable data object Profile : Screen()
    @Serializable data object Settings : Screen()

    // Booking & Cart
    @Serializable data object Cart : Screen()
    @Serializable data object Bookings : Screen()
    
    @Serializable
    data class ResidentialBookingDetails(
        val partnerId: String,
        val serviceId: String
    ) : Screen()
    @Serializable
    data class ResidentialPayment(
        val partnerId: String,
        val serviceId: String
    ) : Screen()
    @Serializable
    data class ResidentialOrderSummary(
        val partnerId: String,
        val serviceId: String
    ) : Screen()
    @Serializable data object ResidentialSuccess : Screen()
    
    @Serializable data class PaymentMethod(val bookingId: String, val customerId: String, val partnerId: String, val amount: Double) : Screen()
    @Serializable data class CashSuccess(val paymentId: String, val amount: Double) : Screen()
    @Serializable data class CashCollection(val paymentId: String, val bookingId: String, val amount: Double) : Screen()
    @Serializable data class CustomerOtp(val paymentId: String, val bookingId: String, val amount: Double) : Screen()
    @Serializable data class PaidSuccess(val amount: Double) : Screen()
    
    // Location
    @Serializable data object LocationPermission : Screen()
    @Serializable data object MapPicker : Screen()
    @Serializable data class OrderTracking(val orderId: String) : Screen()
    
    // Profile Extended
    @Serializable data object EditProfile : Screen()
    @Serializable data object ShippingAddress : Screen()
    @Serializable data object ChangePassword : Screen()
    @Serializable data object AddAccounts : Screen()
    @Serializable data object ContactUs : Screen()
    @Serializable data object FAQ : Screen()

    // Business
    @Serializable data object BusinessMain : Screen()
    @Serializable data class BusinessSubcategories(val category: String) : Screen()
    @Serializable data class BusinessServices(val subcategory: String) : Screen()
    @Serializable data object BusinessPayment : Screen()
    @Serializable data object BusinessSuccess : Screen()
    
    // Education
    @Serializable data class EducationSubcategories(val category: String) : Screen()
    @Serializable data class EducationCourses(val subcategory: String) : Screen()
    @Serializable data object EducationCart : Screen()
    @Serializable data object EducationSuccess : Screen()
    
    // Lifestyle
    @Serializable data class LifestyleSubcategories(val category: String) : Screen()
    @Serializable data class LifestyleServices(val subcategory: String) : Screen()
    @Serializable data object LifestyleCheckout : Screen()
    @Serializable data object LifestylePayment : Screen()
    @Serializable data object LifestyleSuccess : Screen()
    
    // Tech
    @Serializable data class TechSubcategories(val category: String) : Screen()
    @Serializable data class TechServices(val subcategory: String) : Screen()
    @Serializable data object TechCheckout : Screen()
    @Serializable data object TechPayment : Screen()
    @Serializable data object TechSuccess : Screen()
    
    // Mens Grooming
    @Serializable data class MensSubcategories(val category: String) : Screen()
    @Serializable data class MensServices(val subcategory: String) : Screen()
    @Serializable data object MensCheckout : Screen()
    @Serializable data object MensSuccess : Screen()
    
    // Womens Beauty
    @Serializable data class WomensBeautySubcategories(val category: String) : Screen()
    @Serializable data class WomensBeautyServices(val subcategory: String) : Screen()
    @Serializable data object WomensBeautyOrderSummary : Screen()
    @Serializable data object WomensBeautyPayment : Screen()
    @Serializable data object WomensBeautySuccess : Screen()
    
    // Healthcare
    @Serializable data object HealthcareMain : Screen()
    @Serializable data class HealthcareSubcategories(val category: String) : Screen()
    @Serializable data class HealthcareServices(val subcategory: String) : Screen()
    @Serializable data object HealthcareBooking : Screen()
    @Serializable data object HealthcareOrderSummary : Screen()
    @Serializable data object HealthcarePayment : Screen()
    @Serializable data object HealthcareSuccess : Screen()
    @Serializable data object HealthcareOrderTracking : Screen()
    
    // Food
    @Serializable data object FoodCategories : Screen() // List of restaurants
    @Serializable data class FoodSubCategory(val category: String) : Screen()
    @Serializable data class FoodTypes(val subcategory: String) : Screen()
    @Serializable data class FoodSubType(val typeName: String) : Screen()
    @Serializable data class FoodItems(val restaurantId: String) : Screen() // Restaurant menu
    @Serializable data object FoodCart : Screen()
    @Serializable data class FoodBooking(val restaurantId: String) : Screen()
    @Serializable data object FoodOrderSuccess : Screen()
    @Serializable data class FoodOrderTracking(val orderId: String) : Screen()
    
    // Mechanic
    @Serializable data class MechanicSubcategories(val categoryName: String) : Screen()
    @Serializable data object MechanicBooking : Screen()
    @Serializable data object MechanicSuccess : Screen()
    
    // Mobility
    @Serializable data object MobilityMain : Screen()
    @Serializable data object MobilitySuccess : Screen()
    
    // Essentials (Modules)
    @Serializable data object HomeEssentialsMain : Screen()
    @Serializable data class HomeEssentialsCategory(val categoryId: String, val shopId: String? = null) : Screen()
    @Serializable data class HomeEssentialsItems(val subcategoryId: String, val shopId: String? = null) : Screen()
    @Serializable data object HomeEssentialsCheckout : Screen()
    @Serializable data object HomeEssentialsSuccess : Screen()

    // Luxury Screens
    @Serializable data object LuxurySplash : Screen()
    @Serializable data object LuxuryOnboarding1 : Screen()
    @Serializable data object LuxuryOnboarding2 : Screen()
    @Serializable data object LuxuryOnboarding3 : Screen()
    @Serializable data object LuxuryLogin : Screen()
    @Serializable data object LuxurySignUp : Screen()
    @Serializable data object LuxuryForgotPassword : Screen()
    @Serializable data object LuxuryResetPassword : Screen()
    @Serializable data object LuxuryProfile : Screen()
    @Serializable data object LuxurySignOut : Screen()
}
