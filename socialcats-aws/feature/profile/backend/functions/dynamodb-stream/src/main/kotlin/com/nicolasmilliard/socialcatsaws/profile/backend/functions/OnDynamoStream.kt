package com.nicolasmilliard.socialcatsaws.profile.backend.functions

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.nicolasmilliard.socialcatsaws.eventpublisher.Event
import com.nicolasmilliard.socialcatsaws.eventregistry.EventRegistry
import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.Schema
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class OnDynamoStream(appComponent: AppComponent = DaggerAppComponent.create()) :
  RequestHandler<DynamodbEvent, Unit> {

  private val eventBus = appComponent.getEventBusPublisher()
  private val mapper: ObjectMapper = ObjectMapper()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)

  override fun handleRequest(
    dynamoDbEvent: DynamodbEvent,
    context: Context
  ) {
    logger.debug { "Event received: $dynamoDbEvent" }

    val itemsByType =
      dynamoDbEvent.records.groupBy {
        val newImage = it.dynamodb.newImage
        if (newImage != null) newImage[Schema.SharedAttributes.ITEM_TYPE]!!.s else null
      }
    val newImagesOnly =
      itemsByType[Schema.ImageItem.TYPE]?.filter { it.eventName == EventRegistry.EventType.DynamodbStreamRecord.EventNameInsert }
    if (newImagesOnly.isNullOrEmpty()) {
      logger.debug { "No new Images in this batch" }
      return
    }

    val newDynamoDbEvent = DynamodbEvent()
    newDynamoDbEvent.records = newImagesOnly

    val event = Event(
      EventRegistry.EventSource.UsersRepositoryFanout,
      EventRegistry.EventType.DynamodbStreamRecord.EventDetailTypeImageRecordList,
      listOf(newImagesOnly[0].eventSourceARN),
      mapper.writeValueAsString(newDynamoDbEvent)
    )
    eventBus.publish(listOf(event))
  }
}
