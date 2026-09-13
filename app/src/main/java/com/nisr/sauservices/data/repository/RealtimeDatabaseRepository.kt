package com.nisr.sauservices.data.repository

import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.data.model.SupplyOrder
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.filter.FilterOperation
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import io.github.jan.supabase.realtime.selectAsFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class RealtimeDatabaseRepository {
    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    fun getCurrentUserId(): String? = auth.currentUserOrNull()?.id

    suspend fun placeOrderDirectly(order: OrderModel): Result<String> = try {
        val userId = getCurrentUserId() ?: "anonymous"
        val finalOrder = order.copy(customerId = userId)
        
        val inserted = withContext(Dispatchers.IO) {
            postgrest["orders"].insert(finalOrder) {
                select()
            }.decodeSingle<OrderModel>()
        }
        
        Result.success(inserted.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun placeSupplyOrder(order: SupplyOrder): Result<String> = try {
        val userId = getCurrentUserId() ?: "anonymous"
        val finalOrder = order.copy(userId = userId)
        
        withContext(Dispatchers.IO) {
            postgrest["supply_orders"].insert(finalOrder)
        }
        
        Result.success("Order Placed")
    } catch (e: Exception) {
        Result.failure(e)
    }

    @OptIn(SupabaseExperimental::class)
    fun observeUserActivity(): Flow<List<OrderModel>> {
        val userId = getCurrentUserId() ?: return kotlinx.coroutines.flow.flowOf(emptyList())
        return postgrest["orders"]
            .selectAsFlow(
                primaryKey = OrderModel::id,
                filter = FilterOperation("customer_id", FilterOperator.EQ, userId)
            )
            .catch { 
                android.util.Log.e("REALTIME_ERROR", "Error in observeUserActivity: ${it.message}")
                emit(emptyList()) 
            }
            .flowOn(Dispatchers.IO)
    }
}
