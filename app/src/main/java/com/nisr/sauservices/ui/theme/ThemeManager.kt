package com.nisr.sauservices.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.nisr.sauservices.data.local.SessionManager

object ThemeManager {
    var themeConfig by mutableStateOf(SessionManager.ThemeConfig.SYSTEM)
        private set

    fun initialize(sessionManager: SessionManager) {
        themeConfig = sessionManager.getTheme()
    }

    fun updateTheme(newConfig: SessionManager.ThemeConfig, sessionManager: SessionManager) {
        themeConfig = newConfig
        sessionManager.saveTheme(newConfig)
    }
}
