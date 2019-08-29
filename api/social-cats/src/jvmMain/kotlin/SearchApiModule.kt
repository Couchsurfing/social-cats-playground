package com.nicolasmilliard.socialcats.search

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.create

@Module
internal object SearchApiModule {
    @JvmStatic
    @Provides
    fun searchService(client: OkHttpClient): SearchService {
        val contentType = "application/json; charset=utf-8".toMediaType()

        val retrofit = Retrofit.Builder()
            .baseUrl(PRODUCTION_PROXY)
            .client(client)
            .addConverterFactory(Json.asConverterFactory(contentType))
            .build()

        return retrofit.create()
    }
}
