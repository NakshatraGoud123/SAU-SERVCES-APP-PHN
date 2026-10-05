package com.nisr.sauservices.ui.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VendorsViewModelTest {

    @Test
    fun testViewModelInitialization() {
        val viewModel = VendorsViewModel()
        assertNotNull(viewModel.uiState)
    }
}
