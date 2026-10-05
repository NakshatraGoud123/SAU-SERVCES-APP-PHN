package com.nisr.sauservices.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.nisr.sauservices.data.local.WishlistManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WishlistViewModel(application: Application) : AndroidViewModel(application) {
    private val wishlistManager = WishlistManager(application)

    private val _favorites = MutableStateFlow<Set<String>>(wishlistManager.getWishlist())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    fun toggleFavorite(id: String) {
        val updated = wishlistManager.toggleFavorite(id)
        _favorites.value = updated
    }

    fun isFavorite(id: String): Boolean {
        return wishlistManager.isFavorite(id)
    }
}
