package com.amazonaws.dynamodb.stream.fanout.publisher

import com.nicolasmilliard.services.lambda.runtime.events.DynamodbEvent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.SendMessageRequest

private val log = KotlinLogging.logger {}

public class SqsEventPublisher(
    private val sqs: SqsClient,
    private val queueUrl: String,
    private val json: Json = Json {  },
) : EventPublisher {

    override fun publish(event: DynamodbEvent) {
        log.debug { "Sending events $event to SQS queue $queueUrl" }
        sqs.sendMessage(
            SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(json.encodeToString(event))
                .build()
        )
    }
}
