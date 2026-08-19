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
            postgrest["users"].insert(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(uid: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val user = postgrest["users"].select {
                filter { eq("id", uid) }
            }.decodeSingle<User>()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUserProfile(user: User): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["users"].update(user) {
                filter { eq("id", user.id) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(uid: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            postgrest["users"].delete {
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
            val list = postgrest["categories"].select().decodeList<Category>()
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
    }

    // --- ORDERS ---

    @OptIn(SupabaseExperimental::class)
    fun listenToOrders(userId: String? = null): Flow<List<OrderModel>> {
        val filter = if (userId != null) FilterOperation("user_id", FilterOperator.EQ, userId) else null
        return postgrest["orders"].selectAsFlow(OrderModel::id, filter = filter)
    }

    @OptIn(SupabaseExperimental::class)
    fun listenToCustomerOrders(userId: String): Flow<List<OrderModel>> = listenToOrders(userId)

    @OptIn(SupabaseExperimental::class)
    fun listenToCustomerOrder(orderId: String): Flow<List<OrderModel>> {
        return postgrest["orders"].selectAsFlow(OrderModel::id, filter = FilterOperation("id", FilterOperator.EQ, orderId))
    }

    suspend fun placeOrder(order: OrderModel): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inserted = postgrest["orders"].insert(order) {
                select()
            }.decodeSingle<OrderModel>()
            Result.success(inserted.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
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
    }
}
