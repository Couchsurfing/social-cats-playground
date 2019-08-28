package com.nicolasmilliard.socialcats.search

import com.nicolasmilliard.socialcats.model.SearchUsersResult
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import org.w3c.dom.url.URL
import org.w3c.fetch.RequestInit
import kotlin.browser.window

object FetchSearchService : SearchService {

    private val listUrl = URL("list", PRODUCTION_PROXY).href

    override suspend fun searchUsers(authToken: String, input: String?): SearchUsersResult {
        val authHeader: dynamic = object {}
        if (authToken != null) authHeader[AUTHORIZATION_HEADER] = "$AUTHORIZATION_SCHEME $authToken"
        return window
            .fetch(
                listUrl,
                RequestInit(headers = authHeader)
            )
            .then {
                if (it.status != 200.toShort()) {
                    throw RuntimeException("HTTP ${it.status} ${it.statusText}")
                } else {
                    it.text()
                }
            }.then {
                Json.parse(SearchUsersResult.serializer(), it)
            }
            .await()
    }
}
