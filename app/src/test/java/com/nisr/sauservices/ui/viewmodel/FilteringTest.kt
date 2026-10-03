package com.nisr.sauservices.ui.viewmodel

import com.nisr.sauservices.data.model.Vendor
import com.nisr.sauservices.data.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class FilteringTest {

    @Test
    fun testVendorFiltering() {
        val vendors = listOf(
            Vendor(id = "1", name = "Luxe Spa", category = "Wellness"),
            Vendor(id = "2", name = "Gourmet Cafe", category = "Food")
        )
        val query = "spa"
        val filtered = vendors.filter { 
            it.displayName.contains(query, ignoreCase = true) || 
            it.displayCategory.contains(query, ignoreCase = true) 
        }
        assertEquals(1, filtered.size)
        assertEquals("Luxe Spa", filtered[0].displayName)
    }

    @Test
    fun testProductFiltering() {
        val products = listOf(
            Product(id = "p1", name = "Organic Coffee", price = 150.0, unit = "Cup"),
            Product(id = "p2", name = "Green Tea", price = 100.0, unit = "Cup")
        )
        val query = "coffee"
        val filtered = products.filter { it.name.contains(query, ignoreCase = true) }
        assertEquals(1, filtered.size)
        assertEquals("Organic Coffee", filtered[0].name)
    }
}
