package com.nicolasmilliard.socialcats.search

import com.nicolasmilliard.socialcats.api.buildRetrofit
import okhttp3.OkHttpClient
import retrofit2.create

object SearchServiceModule {
    fun searchService(client: Lazy<OkHttpClient>): SearchService {
        val retrofit = buildRetrofit(client)
        return retrofit.create()
    }
}
