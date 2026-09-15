package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.*
import com.nisr.sauservices.data.model.toSafeUuid
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.withContext

class SupabaseRepository {

    private val client = SupabaseClient.client
    private val postgrest = client.postgrest
    private val auth = client.auth

    // --- AUTHENTICATION ---

    fun getCurrentUserId(): String? = auth.currentUserOrNull()?.id

    // --- USER PROFILE ---

    suspend fun registerUser(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["profiles"].insert(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(uid: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val user = postgrest["profiles"].select {
                filter { eq("id", uid) }
            }.decodeSingle<User>()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUserProfile(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["profiles"].update(user) {
                filter { eq("id", user.id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(uid: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["profiles"].delete {
                filter { eq("id", uid) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- ADDRESSES ---

    suspend fun getAddresses(userId: String): Result<List<Address>> = withContext(Dispatchers.IO) {
        try {
            val list = postgrest["addresses"].select {
                filter { eq("user_id", userId) }
            }.decodeList<Address>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addAddress(address: Address): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["addresses"].insert(address)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- PRODUCTS & CATEGORIES ---

    suspend fun getCategories(): Result<List<Category>> = withContext(Dispatchers.IO) {
        try {
            val list = postgrest["categories"].select {
                order("name", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
            }.decodeList<Category>()
            Result.success(list)
        } catch (e: Exception) {
            android.util.Log.e("REPO_ERROR", "getCategories failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getVendors(category: String? = null): Result<List<Vendor>> = withContext(Dispatchers.IO) {
        try {
            val list = if (category == null) {
                postgrest["vendors"].select().decodeList<Vendor>()
            } else {
                postgrest["vendors"].select {
                    filter { ilike("business_type", category) }
                }.decodeList<Vendor>()
            }
            android.util.Log.d("REPO_DEBUG", "Vendors loaded for $category: ${list.size}")
            Result.success(list)
        } catch (e: Exception) {
            android.util.Log.e("REPO_ERROR", "getVendors failed: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getSubcategories(categoryId: String): Result<List<Map<String, String>>> = withContext(Dispatchers.IO) {
        try {
            val response = postgrest["subcategories"].select {
                filter { eq("category_id", categoryId) }
            }
            Result.success(response.decodeList<Map<String, String>>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getServices(subcategoryId: String): Result<List<ServiceModel>> = withContext(Dispatchers.IO) {
        try {
            val list = postgrest["services"].select {
                filter { eq("subcategory_id", subcategoryId) }
            }.decodeList<ServiceModel>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVendorDetails(vendorId: String): Result<Vendor> = withContext(Dispatchers.IO) {
        try {
            val vendor = postgrest["vendors"].select {
                filter { eq("id", vendorId) }
            }.decodeSingle<Vendor>()
            Result.success(vendor)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByVendor(vendorId: String): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val list = postgrest["products"].select {
                filter { eq("vendor_id", vendorId) }
            }.decodeList<Product>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProducts(categoryId: String? = null): Result<List<Product>> = withContext(Dispatchers.IO) {
        try {
            val list = postgrest["products"].select {
                if (categoryId != null) {
                    filter { eq("category_id", categoryId) }
                }
            }.decodeList<Product>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProductDetails(productId: String): Result<Product> = withContext(Dispatchers.IO) {
        try {
            val product = postgrest["products"].select {
                filter { eq("id", productId) }
            }.decodeSingle<Product>()
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- BOOKINGS ---

    @OptIn(SupabaseExperimental::class)
    fun listenToBookings(userId: String? = null): Flow<List<BookingModel>> {
        val filter = if (userId != null) FilterOperation("user_id", FilterOperator.EQ, userId) else null
        return postgrest["bookings"].selectAsFlow(BookingModel::id, filter = filter)
            .catch { emit(emptyList()) }
    }

    suspend fun bookService(booking: BookingModel): Result<String> = withContext(Dispatchers.IO) {
        try {
            val safeBooking = booking.copy(serviceId = booking.serviceId.toSafeUuid())
            val inserted = postgrest["bookings"].insert(safeBooking) {
                select()
            }.decodeSingle<BookingModel>()
            Result.success(inserted.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    @OptIn(SupabaseExperimental::class)
    fun observeMyBookings(userId: String): Flow<List<BookingModel>> {
        return postgrest["bookings"].selectAsFlow(BookingModel::id, filter = FilterOperation("user_id", FilterOperator.EQ, userId))
            .catch { emit(emptyList()) }
    }

    // --- ORDERS ---

    @OptIn(SupabaseExperimental::class)
    fun listenToOrders(userId: String? = null): Flow<List<OrderModel>> {
        val filter = if (userId != null) FilterOperation("customer_id", FilterOperator.EQ, userId) else null
        return postgrest["orders"].selectAsFlow(OrderModel::id, filter = filter)
            .catch { emit(emptyList()) }
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToCustomerOrder(orderId: String): Flow<List<OrderModel>> {
        return postgrest["orders"].selectAsFlow(OrderModel::id, filter = FilterOperation("id", FilterOperator.EQ, orderId))
            .catch { emit(emptyList()) }
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToPartnerLocation(partnerId: String): Flow<List<PartnerLocation>> {
        return postgrest["partner_locations"].selectAsFlow(
            PartnerLocation::partnerId,
            filter = FilterOperation("partner_id", FilterOperator.EQ, partnerId)
        ).catch { emit(emptyList()) }
    }

    suspend fun placeOrder(order: OrderModel, items: List<OrderItem>): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inserted = postgrest["orders"].insert(order) {
                select()
            }.decodeSingle<OrderModel>()
            
            // Insert order items
            val orderItemsList = items.map { it.copy(orderId = inserted.id) }
            postgrest["order_items"].insert(orderItemsList)
            
            Result.success(inserted.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToLocation(id: String, isWorker: Boolean): Flow<List<LiveLocation>> {
        val table = if (isWorker) "worker_locations" else "delivery_locations"
        return postgrest[table].selectAsFlow(LiveLocation::timestamp, filter = FilterOperation("user_id", FilterOperator.EQ, id))
            .catch { emit(emptyList()) }
    }

    // --- PAYMENTS ---

    suspend fun savePayment(payment: Payment): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["payments"].insert(payment)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- REVIEWS ---

    suspend fun addReview(review: Review): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["reviews"].insert(review)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- NOTIFICATIONS ---

    @OptIn(SupabaseExperimental::class)
    fun getNotifications(userId: String): Flow<List<Notification>> {
        return postgrest["notifications"].selectAsFlow(Notification::id, filter = FilterOperation("user_id", FilterOperator.EQ, userId))
            .catch { emit(emptyList()) }
    }

    // --- REAL-TIME CHAT ---

    suspend fun sendMessage(message: ChatMessage): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["messages"].insert(message)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToMessages(orderId: String): Flow<List<ChatMessage>> {
        return postgrest["messages"].selectAsFlow(
            ChatMessage::id,
            filter = FilterOperation("order_id", FilterOperator.EQ, orderId)
        ).catch { emit(emptyList()) }
    }

    // --- WALLET ---

    suspend fun getTransactions(): Result<List<Transaction>> = withContext(Dispatchers.IO) {
        val uid = auth.currentUserOrNull()?.id ?: return@withContext Result.failure(Exception("Not logged in"))
        try {
            val list = postgrest["transactions"].select {
                filter { eq("user_id", uid) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }.decodeList<Transaction>()
            Result.success(list)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWalletBalance(): Result<Double> = withContext(Dispatchers.IO) {
        val uid = auth.currentUserOrNull()?.id ?: return@withContext Result.failure(Exception("Not logged in"))
        try {
            val response = postgrest["wallet_balances"].select {
                filter { eq("user_id", uid) }
            }.decodeSingleOrNull<Map<String, Double>>()
            Result.success(response?.get("balance") ?: 0.0)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateWalletBalance(amount: Double, type: String): Result<Unit> = withContext(Dispatchers.IO) {
        val uid = auth.currentUserOrNull()?.id ?: return@withContext Result.failure(Exception("Not logged in"))
        try {
            // Get current balance
            val current = getWalletBalance().getOrDefault(0.0)
            val newBalance = if (type == "credit") current + amount else current - amount
            
            if (newBalance < 0 && type == "debit") {
                return@withContext Result.failure(Exception("Insufficient wallet balance"))
            }

            postgrest["wallet_balances"].upsert(mapOf(
                "user_id" to uid,
                "balance" to newBalance,
                "updated_at" to System.currentTimeMillis()
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logTransaction(amount: Double, type: String, description: String): Result<Unit> = withContext(Dispatchers.IO) {
        val uid = auth.currentUserOrNull()?.id ?: return@withContext Result.failure(Exception("Not logged in"))
        try {
            val tx = Transaction(
                userId = uid,
                amount = amount,
                type = type,
                description = description
            )
            postgrest["transactions"].insert(tx)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
