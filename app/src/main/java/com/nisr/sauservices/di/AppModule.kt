package com.nisr.sauservices.di

import android.content.Context
import com.nisr.sauservices.data.api.SupabaseClient
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.repository.SupabaseRepository
import com.nisr.sauservices.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): io.github.jan.supabase.SupabaseClient = SupabaseClient.client

    @Provides
    @Singleton
    fun provideUserRepository(): UserRepository = UserRepository()

    @Provides
    @Singleton
    fun provideSupabaseRepository(): SupabaseRepository = SupabaseRepository()

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager = SessionManager(context)
}
