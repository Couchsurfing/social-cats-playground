package com.nicolasmilliard.serverlessworkshop.messaging.pagination.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

/**
 * Jackson Json Serializer for [AttributeValue].
 */
internal class AttributeValueSerializer : JsonSerializer<AttributeValue>() {
    override fun serialize(
        attributeValue: AttributeValue,
        jsonGenerator: JsonGenerator,
        serializerProvider: SerializerProvider
    ) {
        jsonGenerator.writeStartObject()
        if (attributeValue.s() != null) {
            jsonGenerator.writeStringField("s", attributeValue.s())
            jsonGenerator.writeEndObject()
            return
        }
        if (attributeValue.n() != null) {
            jsonGenerator.writeStringField("n", attributeValue.n())
            jsonGenerator.writeEndObject()
            return
        }
        if (attributeValue.b() != null) {
            jsonGenerator.writeStringField("b", attributeValue.b().asUtf8String())
            jsonGenerator.writeEndObject()
            return
        }
        val ss: List<String> = attributeValue.ss()
        if (ss.isNotEmpty()) {
            jsonGenerator.writeObjectField("ss", attributeValue.ss())
            jsonGenerator.writeEndObject()
            return
        }
        val ns: List<String> = attributeValue.ns()
        if (ns.isNotEmpty()) {
            jsonGenerator.writeObjectField("ns", attributeValue.ns())
            jsonGenerator.writeEndObject()
            return
        }
        val bs: List<SdkBytes> = attributeValue.bs()
        if (bs.isNotEmpty()) {
            jsonGenerator.writeObjectField("bs", attributeValue.bs())
            jsonGenerator.writeEndObject()
            return
        }
        val mapValue: Map<String, AttributeValue> = attributeValue.m()
        if (mapValue.isNotEmpty()) {
            jsonGenerator.writeObjectField("m", attributeValue.m())
            jsonGenerator.writeEndObject()
            return
        }
        val attributeValueList: List<AttributeValue> = attributeValue.l()
        if (attributeValueList.isNotEmpty()) {
            jsonGenerator.writeArrayFieldStart("l")
            for (s in attributeValue.l()) {
                jsonGenerator.writeObject(s)
            }
            jsonGenerator.writeEndArray()
            jsonGenerator.writeEndObject()
            return
        }
        if (attributeValue.bool() != null) {
            jsonGenerator.writeBooleanField("bool", attributeValue.bool())
            jsonGenerator.writeEndObject()
            return
        }
        if (attributeValue.nul() != null) {
            jsonGenerator.writeBooleanField("nul", attributeValue.nul())
            jsonGenerator.writeEndObject()
        }
    }
}
