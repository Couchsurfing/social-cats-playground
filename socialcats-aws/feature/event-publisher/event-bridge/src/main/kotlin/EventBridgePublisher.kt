package com.nicolasmilliard.socialcatsaws.eventpublisher.eventbridge

import com.nicolasmilliard.socialcatsaws.eventpublisher.BatchEventPublisher
import com.nicolasmilliard.socialcatsaws.eventpublisher.Event
import mu.KotlinLogging
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry
import java.time.Clock
import java.time.Instant

private val log = KotlinLogging.logger {}

public class EventBridgePublisher(
  private val eventBridge: EventBridgeRetryClient,
  private val failedEventPublisher: BatchEventPublisher,
  private val eventBusName: String,
  private val clock: Clock = Clock.systemUTC()
) : BatchEventPublisher {

  public override fun publish(events: List<Event>) {
    val time: Instant = Instant.now(clock)
    val requestEntries = events
      .asSequence()
      .map { event ->
        PutEventsRequestEntry.builder()
          .eventBusName(eventBusName)
          .time(time)
          .source(event.source)
          .detailType(event.detailType)
          .detail(event.content)
          .resources(event.sourceArns)
          .build()
      }
      .toList().chunked(10) // Max PutEvents
    var failedEntries = mutableListOf<PutEventsRequestEntry>()
    requestEntries.forEach {
      failedEntries.addAll(
        eventBridge.putEvents(
          PutEventsRequest.builder()
            .entries(it)
            .build()
        )
      )
    }

    if (failedEntries.isNotEmpty()) {
      val failedEvents = failedEntries.map {
        Event(
          it.source(),
          it.detailType(),
          it.resources(),
          it.detail()
        )
      }
      failedEventPublisher.publish(failedEvents)
    }
  }
}
