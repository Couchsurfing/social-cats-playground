package com.amazonaws.dynamodb.stream.fanout.publisher

import com.nicolasmilliard.services.lambda.runtime.events.DynamodbEvent
import com.nicolasmilliard.services.lambda.runtime.events.DynamodbStreamRecord
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry
import java.time.Clock
import java.time.Instant
import java.util.Collections
import java.util.stream.Collectors

private val log = KotlinLogging.logger {}

public class EventBridgePublisher(
    private val eventBridge: EventBridgeRetryClient,
    private val failedEventPublisher: EventPublisher,
    private val eventBusName: String,
    private val clock: Clock = Clock.systemUTC(),
    private val json: Json = Json {  }
) : EventPublisher {

    override fun publish(event: DynamodbEvent) {
        val time: Instant = Instant.now(clock)
        val requestEntries: List<PutEventsRequestEntry> = event.Records
            .stream()
            .map { record: DynamodbStreamRecord ->
                PutEventsRequestEntry.builder()
                    .eventBusName(eventBusName)
                    .time(time)
                    .source(EVENT_SOURCE)
                    .detailType(EVENT_DETAIL_TYPE)
                    .detail(toString(record))
                    .resources(record.eventSourceARN)
                    .build()
            }
            .collect(Collectors.toList())
        val failedEntries: List<PutEventsRequestEntry> = eventBridge.putEvents(
            PutEventsRequest.builder()
                .entries(requestEntries)
                .build()
        )
        if (failedEntries.isNotEmpty()) {
            log.debug("Sending failed events {} to failed event publisher", failedEntries)
            failedEntries.forEach { entry: PutEventsRequestEntry -> publishFailedEvent(entry) }
        }
    }

    private fun toString(record: DynamodbStreamRecord): String {
        return json.encodeToString(record)
    }

    private fun publishFailedEvent(entry: PutEventsRequestEntry) {
        val record: DynamodbStreamRecord = json.decodeFromString(entry.detail())
        val failedEvent = DynamodbEvent(Collections.singletonList(record))
        failedEventPublisher.publish(failedEvent)
    }

    public companion object {
        public const val EVENT_SOURCE: String = "aws-dynamodb-stream-eventbridge-fanout"
        public const val EVENT_DETAIL_TYPE: String = "dynamodb-stream-event"
    }
}