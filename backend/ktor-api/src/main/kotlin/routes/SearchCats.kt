package com.nicolasmilliard.socialcats.searchapi.routes

import com.nicolasmilliard.socialcats.search.SearchUseCase
import com.nicolasmilliard.socialcats.searchapi.FirebaseAuthKey
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.search(searchUseCase: SearchUseCase) = authenticate(FirebaseAuthKey) {
    get("/v1/search") {
        val input = call.request.queryParameters["input"] ?: ""
        withContext(Dispatchers.IO) {
            val searchUsers = searchUseCase.searchUsers(input)

            call.respond(searchUsers)
        }
    }
}
