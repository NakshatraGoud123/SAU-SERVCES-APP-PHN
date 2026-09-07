package com.nisr.sauservices.navigation

@Deprecated("Use Screen sealed class instead for Type-Safe navigation")
object Routes {
    // Auth & Onboarding
    const val ONBOARDING = "onboarding"
    const val ROLE_SELECTION = "role_selection"
    const val AUTH_OPTIONS = "auth_options/{role}"
    const val LOGIN = "login/{role}"
    const val SIGNUP = "signup/{role}"
    const val REGISTER = "register"
    const val REGISTER_CUSTOMER = "register_customer"
    const val FORGOT_PASSWORD = "forgot_password"

    // Partner Registration & Dashboards
    const val SHOPKEEPER_REGISTER = "shopkeeper_register"
    const val SERVICE_WORKER_REGISTER = "service_worker_register"
    const val DELIVERY_PARTNER_REGISTER = "delivery_partner_register"
    const val SHOPKEEPER_DASHBOARD = "shopkeeper_dashboard"
    const val SERVICE_WORKER_DASHBOARD = "service_worker_dashboard"
    const val DELIVERY_DASHBOARD = "delivery_dashboard"

    // Main Core
    const val HOME = "home"
    const val CATEGORIES = "categories"
    const val BOOKINGS = "bookings"
    const val CART = "cart" // Unified Cart
    const val SEARCH_RESULTS = "search_results/{query}"
    const val MY_ORDERS = "my_orders"

    // Profile
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "profile/edit"
    const val NOTIFICATIONS = "profile/notifications"
    const val SHIPPING_ADDRESS = "profile/address"
    const val CHANGE_PASSWORD = "profile/change-password"
    const val ADD_ACCOUNTS = "profile/add-accounts"
    const val CONTACT_US = "profile/contact"
    const val FAQ = "profile/faq"

    // Home Essentials (Separate Cart)
    const val ESSENTIALS_MAIN = "home_essentials_main"
    const val ESSENTIALS_CATEGORY = "home_essentials_category/{categoryId}"
    const val ESSENTIALS_ITEMS = "home_essentials_items/{subcategoryId}"
    const val ESSENTIALS_CART = "home_essentials_cart"
    const val ESSENTIALS_CHECKOUT = "home_essentials_checkout"
    const val ESSENTIALS_SUCCESS = "home_essentials_success"

    // Food & Beverages (Separate Cart)
    const val FOOD_CATEGORIES = "food_categories"
    const val FOOD_SUBCATEGORIES = "food_subcategories/{category}"
    const val FOOD_TYPES = "food_types/{subcategory}"
    const val FOOD_ITEMS = "food_items/{type}"
    const val FOOD_CART = "food_cart"
    const val FOOD_CHECKOUT = "food_checkout"
    const val FOOD_BOOKING = "food_booking/{service}"
    const val FOOD_SUCCESS = "food_order_success"

    // Unified Services
    const val RESIDENTIAL_CATEGORIES = "res_categories"
    const val RESIDENTIAL_SUBCATEGORIES = "res_subcategories/{categoryId}"
    const val RESIDENTIAL_SERVICES = "res_services/{categoryId}/{subcategoryId}"

    const val BUSINESS_SUBCATEGORY = "biz_subcategories/{category}"
    const val BUSINESS_SERVICES = "biz_services/{subcategory}"

    const val LIFESTYLE_SUBCATEGORY = "life_subcategories/{category}"
    const val LIFESTYLE_SERVICES = "life_services/{subcategory}"

    const val TECH_SUBCATEGORY = "tech_subcategories/{category}"
    const val TECH_SERVICES = "tech_services/{subcategory}"

    const val MENS_SUBCATEGORIES = "mens_subcategories/{category}"
    const val MENS_SERVICES = "mens_services/{subcategory}"

    const val WOMENS_SUBCATEGORIES = "womens_beauty_subcategories/{category}"
    const val WOMENS_SERVICES = "womens_beauty_services/{subcategory}"

    const val HEALTHCARE_SUBCATEGORIES = "health_subcategories/{category}"
    const val HEALTHCARE_SERVICES = "health_services/{subcategory}"

    // Education
    const val EDUCATION_SUBCATEGORY = "edu_subcategories/{category}"
    const val EDUCATION_COURSES = "edu_courses/{subcategory}"
    const val EDUCATION_CART = "edu_cart"
    const val EDUCATION_BOOKING = "edu_booking"
    const val EDUCATION_SUCCESS = "edu_success"

    // Shared Service Booking Flow (Unified)
    const val SERVICE_BOOKING_DETAILS = "service_booking_details"
    const val SERVICE_PAYMENT = "service_payment"
    const val SERVICE_ORDER_SUMMARY = "service_order_summary"
    const val SERVICE_BOOKING_SUCCESS = "service_booking_success"
    const val PAID_SUCCESS = "paid_success/{amount}"
    
    // New Modules
    const val ESSENTIAL_SUPPLIES = "essential_supplies"
    const val BOOKINGS_MODULE = "bookings_module"

    // Property & Lifestyle Services (PLS)
    const val PLS_MAIN = "PLS_main"
    const val PLS_SUBCATEGORIES = "PLS_subcategories/{category}"
    const val PLS_SERVICES = "PLS_services/{subcategory}"
    const val PLS_BOOKING = "PLS_booking/{serviceId}"
    const val PLS_CHECKOUT = "PLS_checkout"
    const val PLS_SUCCESS = "PLS_success"

    // Admin Panel
    const val ADMIN_DASHBOARD = "ADMIN_dashboard"
    const val ADMIN_BOOKINGS = "ADMIN_bookings"
    const val ADMIN_SERVICES = "ADMIN_services"

    // Worker Side (PLS)
    const val WORKER_DASHBOARD = "WORKER_dashboard"
    const val WORKER_JOBS = "WORKER_jobs"
}
