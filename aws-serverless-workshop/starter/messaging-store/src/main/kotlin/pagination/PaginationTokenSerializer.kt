package com.nicolasmilliard.serverlessworkshop.messaging.pagination

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class PaginationTokenSerializer :
    TokenSerializer<Map<String, AttributeValue>> {
    private val dynamoDbStartKeySerializer: TokenSerializer<Map<String, AttributeValue>>

    init {
        dynamoDbStartKeySerializer = DynamoDbStartKeySerializer()
    }

    override fun deserialize(token: String): Map<String, AttributeValue> {
        return dynamoDbStartKeySerializer.deserialize(token)
    }

    override fun serialize(token: Map<String, AttributeValue>): String {
        return dynamoDbStartKeySerializer.serialize(token)
    }
}
