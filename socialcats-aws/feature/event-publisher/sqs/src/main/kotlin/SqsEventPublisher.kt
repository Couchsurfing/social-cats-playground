package com.nicolasmilliard.socialcatsaws.eventpublisher.sqs

import com.nicolasmilliard.socialcatsaws.eventpublisher.BatchEventPublisher
import com.nicolasmilliard.socialcatsaws.eventpublisher.Event
import mu.KotlinLogging
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.BatchResultErrorEntry
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry

private val log = KotlinLogging.logger {}

public class SqsEventPublisher(
  private val sqs: SqsClient,
  private val queueUrl: String,
) : BatchEventPublisher {

  override fun publish(events: List<Event>) {
    log.debug { "Sending events $events to SQS queue $queueUrl" }
    val sendMessageRequests = events.mapIndexed { index, event ->
      SendMessageBatchRequestEntry.builder()
        .id(index.toString())
        .messageBody(event.content)
        .build()
    }.chunked(10) // SQS Max request

    val failedEntries = mutableListOf<BatchResultErrorEntry>()
    sendMessageRequests.forEach {
      val responses = sqs.sendMessageBatch(
        SendMessageBatchRequest.builder()
          .entries(it)
          .queueUrl(queueUrl)
          .build()
      )
      failedEntries.addAll(responses.failed())
    }
    if (failedEntries.isNotEmpty()) {
      failedEntries.forEach {
        log.error {
          "Failed to sens SQS message: senderFault ${it.senderFault()}, id: ${it.id()}, code: ${it.code()}, message: ${it.message()}\nEvent:\n${
          events[it.id().toInt()]
          }"
        }
      }
    }
  }
}
