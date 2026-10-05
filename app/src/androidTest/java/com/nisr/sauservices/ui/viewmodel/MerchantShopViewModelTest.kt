package com.nisr.sauservices.ui.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MerchantShopViewModelTest {

    @Test
    fun testViewModelInitialization() {
        val viewModel = MerchantShopViewModel()
        assertNotNull(viewModel.uiState)
    }
}
