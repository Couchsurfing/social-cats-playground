package com.nicolasmilliard.socialcats.searchapi

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(arrayOf("-config=backend/socialcats-search-api/debug.conf"))
