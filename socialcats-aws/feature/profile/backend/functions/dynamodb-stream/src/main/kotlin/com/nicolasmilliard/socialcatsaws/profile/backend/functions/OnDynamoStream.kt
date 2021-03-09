package com.nicolasmilliard.socialcatsaws.profile.backend.functions

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.nicolasmilliard.services.lambda.runtime.events.DynamodbEvent
import kotlinx.serialization.decodeFromString
import mu.KotlinLogging
import java.io.InputStream
import java.io.OutputStream

private val logger = KotlinLogging.logger {}

class OnDynamoStream(appComponent: AppComponent = DaggerAppComponent.create()) : RequestStreamHandler {

  private val eventPublisher = appComponent.getEventPublisher()
  private val json = appComponent.getJson()

  override fun handleRequest(
    input: InputStream, output: OutputStream,
    context: Context
  ) {

    output.bufferedWriter().use { _ ->
      val inputText = input.bufferedReader().use { it.readText() }
      logger.debug { inputText }
      val event: DynamodbEvent = json.decodeFromString(inputText)
      eventPublisher.publish(event)
    }
  }
}
