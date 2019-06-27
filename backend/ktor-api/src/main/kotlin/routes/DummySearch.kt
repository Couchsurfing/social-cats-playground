package com.nicolasmilliard.socialcats.searchapi.routes

import com.nicolasmilliard.socialcats.search.SearchUseCase
import io.ktor.application.call
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Location("/search")
data class Query(val input: String, val page: Int = 1, val count: Int = 20)

// fun Route.dummySearch(searchUseCase: SearchUseCase) {
//    get<Query> {
//        withContext(Dispatchers.IO) {
//            searchUseCase.searchUser(it.input)
//        }
//        call.respondText("Search ${it.input}", contentType = ContentType.Text.Plain)
//    }
// }

fun Route.dummySearch2(searchUseCase: SearchUseCase) {
    get("/search2") {
        val queryParameters: Parameters = call.request.queryParameters
        val input = call.request.queryParameters["input"] ?: ""
        withContext(Dispatchers.IO) {
            val searchUsers = searchUseCase.searchUsers(input)
            call.respond(searchUsers)
        }
    }
}
