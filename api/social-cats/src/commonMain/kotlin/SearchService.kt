package com.nicolasmilliard.socialcats.search

import com.nicolasmilliard.socialcats.model.SearchUsersResult

const val PRODUCTION_PROXY = "https://searchapi-dot-sweat-monkey.appspot.com/"
const val AUTHORIZATION_HEADER = "Authorization"
const val AUTHORIZATION_SCHEME = "Bearer"

expect interface SearchService {
    suspend fun searchUsers(authToken: String?, query: String?): SearchUsersResult
}
