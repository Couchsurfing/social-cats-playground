package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.nicolasmilliard.socialcatsaws.eventsource.BatchEventSource

class FakeSqsEventSource : BatchEventSource<SQSEvent.SQSMessage>{
    var processedEvents: MutableList<SQSEvent.SQSMessage> = mutableListOf()
    override fun markEventAsProcessed(events: List<SQSEvent.SQSMessage>) {
        processedEvents.addAll(events)
    }

    var udpatedEventsTimeout: MutableMap<SQSEvent.SQSMessage, Long?> = mutableMapOf()
    override fun updateEventTimeoutVisibility(events: Map<SQSEvent.SQSMessage, Long?>) {
        udpatedEventsTimeout.putAll(events)
    }
}