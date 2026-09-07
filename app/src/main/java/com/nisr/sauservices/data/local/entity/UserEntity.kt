package com.nisr.sauservices.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val phone: String? = null,
    val name: String? = null,
    val role: String,
    val avatarUrl: String? = null
)
