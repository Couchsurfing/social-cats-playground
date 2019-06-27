package com.nicolasmilliard.socialcats.searchapi

import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.search() = authenticate(FirebaseAuthKey) {
  get("/v1/search") {
    call.respondText("Hello", contentType = ContentType.Text.Plain)
  }
}
