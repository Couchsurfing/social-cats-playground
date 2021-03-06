package com.nicolasmilliard.socialcats.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

actual fun runTest(body: suspend CoroutineScope.() -> Unit) = runBlocking { body() }
