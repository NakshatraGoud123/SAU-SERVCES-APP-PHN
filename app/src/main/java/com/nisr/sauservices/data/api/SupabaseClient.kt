package com.nisr.sauservices.data.api

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.functions.Functions
import io.ktor.client.engine.okhttp.OkHttp

object SupabaseClient {
    const val SUPABASE_URL = "https://vpadhrxammaxitlcrauj.supabase.co"
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InZwYWRocnhhbW1heGl0bGNyYXVqIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODUwNjcwNzYsImV4cCI6MjEwMDY0MzA3Nn0.C22riAm5-qsWoxB15gBMZ48tck2sUbqetS2KGw2uDMM"

    val client: io.github.jan.supabase.SupabaseClient = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        httpEngine = OkHttp.create()
        install(Auth)
        install(Postgrest)
        install(Realtime)
        install(Storage)
        install(Functions)
    }
}
