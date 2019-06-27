package com.nicolasmilliard.socialcats.search

import com.nicolasmilliard.socialcats.model.SearchUsersResult
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

actual interface SearchService {
    @GET("search2")
    actual suspend fun searchUsers(@Header("Authorization") authToken: String?, @Query("input") query: String?): SearchUsersResult
}
