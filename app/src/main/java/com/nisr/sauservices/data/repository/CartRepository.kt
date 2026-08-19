package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.CartModel
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.toSafeUuid
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CartRepository {
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    private fun getUserId(): String? {
        return auth.currentUserOrNull()?.id
    }

    suspend fun addToCart(item: CartModel): Result<Unit> = try {
        val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))
        val insertData = com.nisr.sauservices.data.model.CartItemInsert(
            userId = userId,
            productId = item.productId.toSafeUuid(),
            itemName = item.itemName,
            price = item.price,
            unit = item.unit,
            quantity = item.quantity,
            category = item.category,
            subcategory = item.subcategory,
            totalPrice = item.totalPrice,
            date = item.date,
            time = item.time,
            timestamp = item.timestamp,
        )
        withContext(Dispatchers.IO) {
            postgrest["cart_items"].insert(insertData)
        }
        Result.success(Unit)
    } catch (e: Exception) {
        android.util.Log.e("CART_REPO", "Add failed: ${e.message}", e)
        Result.failure(e)
    }

    suspend fun updateQuantity(itemId: String, newQuantity: Int): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            if (newQuantity <= 0) {
                postgrest["cart_items"].delete {
                    filter {
                        eq("id", itemId)
                    }
                }
            } else {
                postgrest["cart_items"].update(
                    update = {
                        set("quantity", newQuantity)
                    },
                ) {
                    filter {
                        eq("id", itemId)
                    }
                }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    @OptIn(SupabaseExperimental::class, ExperimentalCoroutinesApi::class)
    fun getCartItems(): Flow<List<CartModel>> {
        return auth.sessionStatus.flatMapLatest { status ->
            val userId = if (status is SessionStatus.Authenticated) status.session.user?.id else null
            if (userId == null) {
                flowOf(emptyList())
            } else {
                postgrest["cart_items"]
                    .selectAsFlow(
                        primaryKey = CartModel::itemId,
                        filter = io.github.jan.supabase.postgrest.query.filter.FilterOperation(
                            "user_id",
                            io.github.jan.supabase.postgrest.query.filter.FilterOperator.EQ,
                            userId
                        ),
                    )
                    .map { list: List<CartModel> ->
                        list.filter { it.itemId.isNotEmpty() }
                    }
            }
        }
    }

    suspend fun removeItem(itemId: String) {
        withContext(Dispatchers.IO) {
            try {
                postgrest["cart_items"].delete {
                    filter {
                        eq("id", itemId)
                    }
                }
            } catch (_: Exception) {
                // handle error
            }
        }
    }

    suspend fun clearCart(): Result<Unit> = try {
        val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))
        withContext(Dispatchers.IO) {
            postgrest["cart_items"].delete {
                filter {
                    eq("user_id", userId)
                }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    @Suppress("unused")
    suspend fun placeOrder(order: OrderModel): Result<String> {
        return try {
            val userId = getUserId() ?: return Result.failure(Exception("User not logged in"))
            val finalOrder = order.copy(userId = userId, status = "placed")
            
            val insertedOrder = withContext(Dispatchers.IO) {
                postgrest["orders"].insert(finalOrder) {
                    select()
                }.decodeSingle<OrderModel>()
            }

            order.items.filter { it.unit == "Booking" }.forEach { cartItem ->
                val bookingData = BookingModel(
                    userId = userId,
                    serviceId = cartItem.productId.toSafeUuid(),
                    serviceName = cartItem.itemName,
                    scheduleDate = cartItem.date ?: "",
                    scheduleTime = cartItem.time ?: "",
                    status = "pending",
                    address = order.address
                )
                withContext(Dispatchers.IO) {
                    postgrest["bookings"].insert(bookingData)
                }
            }

            Result.success(insertedOrder.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @Suppress("unused")
    suspend fun updateOrderStatus(orderId: String, newStatus: String, staffId: String? = null): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["orders"].update(
                update = {
                    set("status", newStatus)
                    staffId?.let { set("delivery_partner_id", it) }
                }
            ) {
                filter {
                    eq("id", orderId)
                }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    @Suppress("unused")
    suspend fun updateBookingStatus(bookingId: String, newStatus: String, workerId: String? = null): Result<Unit> = try {
        withContext(Dispatchers.IO) {
            postgrest["bookings"].update(
                update = {
                    set("status", newStatus)
                    workerId?.let { set("provider_id", it) }
                }
            ) {
                filter {
                    eq("id", bookingId)
                }
            }
        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    @OptIn(SupabaseExperimental::class)
    @Suppress("unused")
    fun getGlobalOrders(): Flow<List<OrderModel>> {
        return postgrest["orders"]
            .selectAsFlow(OrderModel::id)
    }

    @OptIn(SupabaseExperimental::class)
    @Suppress("unused")
    fun getGlobalBookings(): Flow<List<BookingModel>> {
        return postgrest["bookings"]
            .selectAsFlow(BookingModel::id)
    }
}
