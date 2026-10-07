package com.nisr.sauservices.ui.viewmodel

import com.nisr.sauservices.data.model.BookingModel
import com.nisr.sauservices.data.model.BookingInsertDto
import com.nisr.sauservices.data.model.toInsertDto
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BookingDtoTest {

    @Test
    fun testBookingInsertDtoSerialization() {
        val booking = BookingModel(
            id = "123",
            userId = "user-uuid",
            serviceId = "service-uuid",
            userName = "Test User",
            userPhone = "9876543210",
            userAddress = "123 Main St",
            totalAmount = 500.0,
            address = "123 Main St",
            amount = 500.0,
            totalPrice = 500.0,
            serviceName = "Plumbing",
            timestamp = 123456789L
        )

        val dto = booking.toInsertDto()
        val jsonString = Json.encodeToString<BookingInsertDto>(dto)

        // Verify required fields are present
        assertTrue(jsonString.contains("user_id"))
        assertTrue(jsonString.contains("service_id"))
        assertTrue(jsonString.contains("total_amount"))
        assertTrue(jsonString.contains("user_address"))

        // Verify forbidden / generated fields are completely absent from INSERT payload
        assertFalse(jsonString.contains("\"id\""))
        assertFalse(jsonString.contains("\"created_at\""))
        assertFalse(jsonString.contains("\"address\""))
        assertFalse(jsonString.contains("\"amount\""))
        assertFalse(jsonString.contains("\"total_price\""))
        assertFalse(jsonString.contains("\"service_name\""))
        assertFalse(jsonString.contains("\"timestamp\""))
    }
}
