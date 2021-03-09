package com.nicolasmilliard.serverlessworkshop.messaging.pagination.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.util.ArrayList
import java.util.HashMap

/**
 * Jackson Json Deserializer for [AttributeValue].
 */
@Suppress("UNCHECKED_CAST")
internal class AttributeValueDeserializer :
    JsonDeserializer<AttributeValue>() {
    override fun deserialize(
        jsonParser: JsonParser,
        deserializationContext: DeserializationContext
    ): AttributeValue {
        val oc = jsonParser.codec
        val node = oc.readTree<JsonNode>(jsonParser)
        val result = AttributeValue.builder()
        var value: JsonNode? = node["s"]
        if (value != null) {
            result.s(value.asText())
            return result.build()
        }
        value = node["n"]
        if (value != null) {
            result.n(value.asLong().toString())
            return result.build()
        }
        value = node["b"]
        if (value != null) {
            result.b(SdkBytes.fromUtf8String(value.asText()))
            return result.build()
        }
        value = node["ss"]
        if (value != null) {
            result.ss(oc.treeToValue(value, List::class.java)as List<String>)
            return result.build()
        }
        value = node["ns"]
        if (value != null) {
            result.ns(oc.treeToValue(value, List::class.java)as List<String>)
            return result.build()
        }
        value = node["bs"]
        if (value != null) {
            val iterator: Iterator<JsonNode> = value.iterator()
            val values: MutableList<SdkBytes> = ArrayList()
            while (iterator.hasNext()) {
                values.add(oc.treeToValue(iterator.next(), SdkBytes::class.java))
            }
            result.bs(values)
            return result.build()
        }
        value = node["m"]
        if (value != null) {
            val iterator = value.fieldNames()
            val values: MutableMap<String, AttributeValue> = HashMap()
            while (iterator.hasNext()) {
                val fieldName = iterator.next()
                values[fieldName] = oc.treeToValue(value[fieldName], AttributeValue::class.java)
            }
            result.m(values)
            return result.build()
        }
        value = node["l"]
        if (value != null) {
            val iterator: Iterator<JsonNode> = value.iterator()
            val values: MutableList<AttributeValue> = ArrayList()
            while (iterator.hasNext()) {
                values.add(oc.treeToValue(iterator.next(), AttributeValue::class.java))
            }
            result.l(values)
            return result.build()
        }
        value = node["bool"]
        if (value != null) {
            result.bool(value.asBoolean())
            return result.build()
        }
        value = node["nul"]
        if (value != null) {
            result.nul(value.asBoolean())
            return result.build()
        }
        return result.build()
    }
}
