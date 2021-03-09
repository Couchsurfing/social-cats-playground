package com.amazonaws.dynamodb.stream.fanout.publisher

import com.nicolasmilliard.services.lambda.runtime.events.DynamodbEvent

/**
 * Publishing event.
 */
public interface EventPublisher {
    /**
     * Publish DynamoDB stream event.
     * @param event DynamoDB stream event.
     */
    public  fun publish(event: DynamodbEvent)
}