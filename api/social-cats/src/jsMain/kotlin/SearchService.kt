package com.nicolasmilliard.socialcats.search

import com.nicolasmilliard.socialcats.model.SearchUsersResult

actual interface SearchService {
    actual suspend fun searchUsers(authToken: String?, query: String?): SearchUsersResult
}
