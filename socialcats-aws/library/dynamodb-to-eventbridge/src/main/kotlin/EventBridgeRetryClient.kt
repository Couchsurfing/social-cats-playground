package com.amazonaws.dynamodb.stream.fanout.publisher

import mu.KotlinLogging
import software.amazon.awssdk.services.eventbridge.EventBridgeClient
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequest
import software.amazon.awssdk.services.eventbridge.model.PutEventsRequestEntry
import software.amazon.awssdk.services.eventbridge.model.PutEventsResponse
import java.util.stream.Collectors
import java.util.stream.IntStream

private val log = KotlinLogging.logger {}
public class EventBridgeRetryClient(private val eventBridge: EventBridgeClient, private val maxAttempt: Int = 1) {

    /**
     * Call AWS EventBridge PutEvents API with retries.
     *
     *
     * PutEvents API puts a list of events. Some of them may fail.
     * The API returns information on the status of each event.
     * This method only retries the failed events until the maximum attempt count is met.
     *
     * @param request PutEvents API request.
     * @return a list of PutEventsRequestEntry that failed after maximum attempts.
     */
    public fun putEvents(request: PutEventsRequest): List<PutEventsRequestEntry> {
        var requestCopy = request
        var attemptCount = 0
        while (attemptCount < maxAttempt) {
            log.debug{"Attempt ${attemptCount + 1} to put events $requestCopy"}
            val response: PutEventsResponse = eventBridge.putEvents(requestCopy)
            if (response.failedEntryCount() == 0) {
                return emptyList()
            }
            val requestEntries = requestCopy.entries()
            val resultEntries = response.entries()
            val failedEntries: List<PutEventsRequestEntry> = IntStream
                .range(0, resultEntries.size)
                .filter { i -> resultEntries[i].errorCode() != null }
                .mapToObj { index: Int -> requestEntries[index] }
                .collect(Collectors.toList())
            requestCopy = PutEventsRequest.builder()
                .entries(failedEntries)
                .build()
            attemptCount++
        }
        return requestCopy.entries()
    }
}