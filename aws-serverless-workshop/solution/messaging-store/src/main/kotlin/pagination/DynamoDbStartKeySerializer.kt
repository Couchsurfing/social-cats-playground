package com.nicolasmilliard.serverlessworkshop.messaging.pagination

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.nicolasmilliard.serverlessworkshop.messaging.pagination.jackson.AttributeValueDeserializer
import com.nicolasmilliard.serverlessworkshop.messaging.pagination.jackson.AttributeValueSerializer
import com.nicolasmilliard.serverlessworkshop.messaging.pagination.jackson.SdkBytesDeserializer
import com.nicolasmilliard.serverlessworkshop.messaging.pagination.jackson.SdkBytesSerializer
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

/**
 * Implementation of [TokenSerializer] to serialize/deserialize
 * DynamoDb's pagination keys (LastEvaluatedKey and ExclusiveStartKey)
 * using Jackson.
 * See doc: https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Query.html#Query.Pagination
 */
public class DynamoDbStartKeySerializer : TokenSerializer<Map<String, AttributeValue>> {
    internal companion object {
        private val OBJECT_MAPPER: ObjectMapper

        init {
            val module = SimpleModule()
            module.addDeserializer(AttributeValue::class.java, AttributeValueDeserializer())
            module.addSerializer(AttributeValue::class.java, AttributeValueSerializer())
            module.addDeserializer(SdkBytes::class.java, SdkBytesDeserializer())
            module.addSerializer(SdkBytes::class.java, SdkBytesSerializer())
            val objectMapper = ObjectMapper()
            objectMapper.registerModule(module)
            OBJECT_MAPPER = objectMapper
        }
    }

    override fun deserialize(token: String): Map<String, AttributeValue> {

        return OBJECT_MAPPER.readValue(
            token,
            object : TypeReference<Map<String, AttributeValue>>() {}
        )
    }

    override fun serialize(token: Map<String, AttributeValue>): String {
        return OBJECT_MAPPER.writeValueAsString(token)
    }
}
