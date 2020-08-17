package com.nicolasmilliard.socialcats.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit

fun buildRetrofit(client: Lazy<OkHttpClient>): Retrofit {
    val contentType = "application/json; charset=utf-8".toMediaType()
    return Retrofit.Builder()
        .baseUrl(PRODUCTION_API)
        .callFactory(object : Call.Factory {
            override fun newCall(request: Request): Call {
                return client.value.newCall(request)
            }
        })
        .addConverterFactory(
            Json{
                encodeDefaults = true
                ignoreUnknownKeys = true
                isLenient = true
//                serializeSpecialFloatingPointValues = true
                allowStructuredMapKeys = true
                prettyPrint = false
                useArrayPolymorphism = true
            }.asConverterFactory(contentType)
        )
        .build()
}
