package com.nisr.sauservices.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Transient

@Serializable
data class OperationResult(
    @SerialName("is_success") val isSuccess: Boolean = false,
    @SerialName("message") val message: String? = null,
    @SerialName("exception") val exception: String? = null
)

@Serializable
data class LiveLocation(
    @SerialName("lat") val lat: Double = 0.0,
    @SerialName("lng") val lng: Double = 0.0,
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis()
)

@Serializable
data class BookingModel(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("user_name") val userName: String = "",
    @SerialName("user_phone") val userPhone: String = "",
    @SerialName("user_address") val userAddress: String = "",
    @SerialName("service_id") val serviceId: String = "",
    @SerialName("service_name") val serviceName: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("subcategory") val subcategory: String = "",
    @SerialName("scheduled_date") val scheduleDate: String = "",
    @SerialName("scheduled_time") val scheduleTime: String = "",
    @SerialName("time_slot") val timeSlot: String = "",
    @SerialName("status") var status: String = "pending",
    @SerialName("provider_id") val providerId: String = "",
    @SerialName("address") val address: String = "",
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("total_price") val totalPrice: Double = 0.0,
    @SerialName("payment_method") val paymentMethod: String = "Cash",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis()
) {
    val bookingId: String get() = id
    val orderStatus: String get() = status
    val displayAddress: String get() = address.ifEmpty { userAddress }.ifEmpty { "No address provided" }
    val displayDate: String get() = scheduleDate.ifEmpty { "TBD" }
    val displayTime: String get() = scheduleTime.ifEmpty { timeSlot }.ifEmpty { "" }
    val displayService: String get() = serviceName.ifEmpty { category }.ifEmpty { "Service Request" }
    
    // UI compatibility fields
    val customerName: String get() = userName
    val customerPhone: String get() = userPhone
    val price: String get() = totalPrice.toString()
    
    // Snake case getters for legacy repository code if any
    val user_id: String get() = userId
    val service_name: String get() = serviceName
    val scheduled_date: String get() = scheduleDate
    val scheduled_time: String get() = scheduleTime
}

@Serializable
data class OrderModel(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("user_name") val userName: String = "",
    @SerialName("shop_id") val shopId: String? = null,
    @SerialName("items") val items: List<CartModel> = emptyList(),
    @SerialName("total_amount") val totalAmount: Double = 0.0,
    @SerialName("total_price") val totalPrice: Double = 0.0,
    @SerialName("address") val address: String = "",
    @SerialName("pickup_address") val pickupAddress: String = "Shop",
    @SerialName("drop_address") val dropAddress: String = "",
    @SerialName("status") var status: String = "placed",
    @SerialName("delivery_partner_id") val deliveryPartnerId: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("timestamp") val timestamp: Long = System.currentTimeMillis(),
    @Transient @SerialName("customer_location") val customerLocation: LiveLocation = LiveLocation(),
    
    // Additional fields for Unified Order/Booking view
    @SerialName("service_name") val serviceName: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("subcategory") val subcategory: String = "",
    @SerialName("scheduled_date") val scheduleDate: String? = null,
    @SerialName("scheduled_time") val scheduleTime: String? = null,
    @SerialName("payment_method") val paymentMethod: String = "Cash"
) {
    val orderId: String get() = id
    val orderStatus: String get() = status
    val amount: Double get() = if (totalPrice > 0) totalPrice else totalAmount
    
    // UI compatibility fields
    val customerName: String get() = userName
    val deliveryId: String get() = id
    
    // Snake case getters
    val user_id: String get() = userId
    val total_amount: Double get() = totalAmount
}

@Serializable
data class SupabaseUser(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("email") val email: String = "",
    @SerialName("phone") val phone: String = "",
    @SerialName("role") val role: String = "customer",
    @SerialName("status") val status: String = "active"
) {
    val displayName: String get() = name.ifEmpty { "User" }
}

@Serializable
data class Category(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("description") val description: String? = null
)

@Serializable
data class Product(
    @SerialName("id") val id: String = "",
    @SerialName("shopkeeper_id") val shopkeeperId: String? = null,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("name") val name: String = "",
    @SerialName("description") val description: String? = null,
    @SerialName("price") val price: Double = 0.0,
    @SerialName("stock_quantity") val stockQuantity: Int = 0,
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class ServiceModel(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("category_id") val categoryId: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("price") val price: Double = 0.0,
    @SerialName("image_url") val imageUrl: String = ""
)

@Serializable
data class Payment(
    @SerialName("id") val id: String = "",
    @SerialName("order_id") val orderId: String? = null,
    @SerialName("booking_id") val bookingId: String? = null,
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("payment_method") val paymentMethod: String = "",
    @SerialName("status") val status: String = "pending",
    @SerialName("transaction_id") val transactionId: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Review(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("service_id") val serviceId: String? = null,
    @SerialName("product_id") val productId: String? = null,
    @SerialName("rating") val rating: Int = 5,
    @SerialName("comment") val comment: String? = null,
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class Notification(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("message") val message: String = "",
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null,
    
    // UI compatibility
    @SerialName("order_updates") val orderUpdates: Boolean = false,
    @SerialName("service_alerts") val serviceAlerts: Boolean = false,
    @SerialName("app_updates") val appUpdates: Boolean = false
)

@Serializable
data class Delivery(
    @SerialName("id") val id: String = "",
    @SerialName("order_id") val orderId: String = "",
    @SerialName("status") var status: String = "Assigned",
    @SerialName("delivery_partner_id") val deliveryPartnerId: String? = null,
    @SerialName("customer_name") val customerName: String = "",
    @SerialName("pickup_address") val pickupAddress: String = "",
    @SerialName("drop_address") val dropAddress: String = "",
    @SerialName("distance") val distance: String = "",
    @SerialName("cart_added_time") val cartAddedTime: String = "3:15 PM",
    @SerialName("items") val items: String = "2 Items",
    @SerialName("otp") val otp: String = "7193",
    @SerialName("payment_mode") val paymentMode: String = "Prepaid",
    @SerialName("pickup_shop") val pickupShop: String = "FreshMart Express",
    @SerialName("eta") val eta: String = "15 min",
    @SerialName("customer_phone") val customerPhone: String = "+91 9876543210",
    @SerialName("order_amount") val orderAmount: String = "₹370"
) {
    val deliveryId: String get() = id
}

@Serializable
data class CashPaymentModel(
    @SerialName("id") val id: String = "",
    @SerialName("booking_id") val bookingId: String = "",
    @SerialName("customer_id") val customerId: String = "",
    @SerialName("partner_id") val partnerId: String = "",
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("commission") val commission: Double = 0.0,
    @SerialName("partner_amount") val partnerAmount: Double = 0.0,
    @SerialName("status") val status: String = "pending", // pending, cash_collected, paid
    @SerialName("otp") val otp: String = "",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("collected_at") val collectedAt: String? = null,
    @SerialName("confirmed_at") val confirmedAt: String? = null
)

@Serializable
data class RazorpayPaymentModel(
    @SerialName("id") val id: String = "",
    @SerialName("booking_id") val bookingId: String = "",
    @SerialName("customer_id") val customerId: String = "",
    @SerialName("partner_id") val partnerId: String = "",
    @SerialName("razorpay_order_id") val razorpayOrderId: String = "",
    @SerialName("razorpay_payment_id") val razorpayPaymentId: String = "",
    @SerialName("razorpay_signature") val razorpaySignature: String = "",
    @SerialName("amount") val amount: Double = 0.0,
    @SerialName("status") val status: String = "authorized", // authorized, captured, failed
    @SerialName("created_at") val createdAt: String? = null
)

@Serializable
data class PLSCategory(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("icon") val icon: Int? = null,
    @SerialName("subcategories") val subcategories: List<String> = emptyList()
)

@Serializable
data class PLSService(
    @SerialName("id") val id: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("price") val price: Double = 0.0,
    @SerialName("unit") val unit: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("subcategory") val subcategory: String = "",
    @SerialName("description") val description: String = "",
    @SerialName("is_enabled") val isEnabled: Boolean = true
)

@Serializable
data class PLSBooking(
    @SerialName("id") val id: String = "",
    @SerialName("user_id") val userId: String = "",
    @SerialName("user_name") val userName: String = "",
    @SerialName("user_phone") val userPhone: String = "",
    @SerialName("user_address") val userAddress: String = "",
    @SerialName("service_id") val serviceId: String = "",
    @SerialName("service_name") val serviceName: String = "",
    @SerialName("category") val category: String = "",
    @SerialName("subcategory") val subcategory: String = "",
    @SerialName("date") val scheduleDate: String = "",
    @SerialName("time_slot") val timeSlot: String = "",
    @SerialName("status") var status: String = "Pending",
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("total_price") val totalPrice: Double = 0.0,
    @SerialName("payment_method") val paymentMethod: String = "",
    @SerialName("guests_count") val guestsCount: Int? = null,
    @SerialName("duration") val duration: Int? = null,
    @SerialName("area_sqft") val areaSqft: Double? = null,
    @SerialName("requirements") val requirements: String? = null,
    @SerialName("assigned_worker_id") val assignedWorkerId: String = "",
    @SerialName("assigned_worker_name") val assignedWorkerName: String = ""
) {
    // UI compatibility
    val user_id: String get() = userId
    val user_name: String get() = userName
    val user_phone: String get() = userPhone
    val user_address: String get() = userAddress
    val service_name: String get() = serviceName
    val date: String get() = scheduleDate
    val time_slot: String get() = timeSlot
    val total_price: Double get() = totalPrice
    val guests_count: Int? get() = guestsCount
    val area_sqft: Double? get() = areaSqft
    val payment_method: String get() = paymentMethod
}
