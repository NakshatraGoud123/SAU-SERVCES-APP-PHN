package com.nisr.sauservices.data.local

import android.content.Context
import android.content.SharedPreferences

class WishlistManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("sau_wishlist_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_WISHLIST_IDS = "wishlist_ids"
    }

    fun getWishlist(): Set<String> {
        return prefs.getStringSet(KEY_WISHLIST_IDS, emptySet()) ?: emptySet()
    }

    fun toggleFavorite(id: String): Set<String> {
        val current = getWishlist().toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        prefs.edit().putStringSet(KEY_WISHLIST_IDS, current).apply()
        return current
    }

    fun isFavorite(id: String): Boolean {
        return getWishlist().contains(id)
    }
}
