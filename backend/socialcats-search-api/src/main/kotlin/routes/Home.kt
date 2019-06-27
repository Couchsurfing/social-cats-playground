package com.nicolasmilliard.socialcats.searchapi

import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.get

fun Route.home() {
  get("/") {
    call.respondText("Hello", contentType = ContentType.Text.Plain)
  }
}
