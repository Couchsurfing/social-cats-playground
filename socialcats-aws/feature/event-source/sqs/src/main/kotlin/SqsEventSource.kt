package com.nicolasmilliard.socialcatsaws.eventsource

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import mu.KotlinLogging
import software.amazon.awssdk.core.retry.RetryPolicy
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.ChangeMessageVisibilityBatchRequestEntry
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry
import java.time.Duration
import kotlin.math.min

private val logger = KotlinLogging.logger {}
public class SqsEventSource(
  private val sqsClient: SqsClient,
  private val sourceQueueUrl: String
) : BatchEventSource<SQSEvent.SQSMessage> {
  override fun markEventAsProcessed(events: List<SQSEvent.SQSMessage>) {
    val entries = events.map { msg ->
      DeleteMessageBatchRequestEntry.builder()
        .id(msg.messageId)
        .receiptHandle(msg.receiptHandle)
        .build()
    }

    val results = sqsClient.deleteMessageBatch {
      it.queueUrl(sourceQueueUrl)
        .entries(entries)
    }
    if (results.hasFailed()) {
      logger.warn { "Sqs failed to Delete successful message for ${results.failed().size} messages" }
      results.failed().forEach {
        logger.error { "Failed to Delete successful message: ${it.id()}, ${it.code()}, ${it.message()} " }
      }
    }
  }

  override fun updateEventTimeoutVisibility(events: Map<SQSEvent.SQSMessage, Long?>) {
    val entries = events.map { entry ->
      val visibilityTimeout = computeVisibilityTimeout(entry.key, entry.value)
      logger.debug { "Setting visibility timeout to: $visibilityTimeout seconds for message id: ${entry.key.messageId}" }
      ChangeMessageVisibilityBatchRequestEntry.builder()
        .id(entry.key.messageId)
        .receiptHandle(entry.key.receiptHandle)
        .visibilityTimeout(visibilityTimeout)
        .build()
    }

    val results = sqsClient.changeMessageVisibilityBatch {
      it.queueUrl(sourceQueueUrl)
      it.entries(entries)
    }
    if (results.hasFailed()) {
      logger.warn { "Sqs failed to change Message Visibility for ${results.failed().size} messages" }
      results.failed().forEach {
        logger.error { "Failed message visibility change: ${it.id()}, ${it.code()}, ${it.message()} " }
      }
    }
  }

  private fun computeVisibilityTimeout(msg: SQSEvent.SQSMessage, suggestedRetryDelay: Long?): Int {
    return if (suggestedRetryDelay != null) {
      min(suggestedRetryDelay, Duration.ofHours(12).toSeconds()).toInt()
    } else {
      val approximateReceivedCount =
        msg.attributes["ApproximateReceiveCount"]!!.toInt()
      RetryPolicy.builder()
        .backoffStrategy()
        .calculateExponentialDelay(
          approximateReceivedCount,
          Duration.ofSeconds(2),
          Duration.ofHours(12)
        )
    }
  }
}
