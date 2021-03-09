@file:UseSerializers(InstantSerializer::class, ByteBufferSerializer::class)
package com.nicolasmilliard.services.lambda.runtime.events.model.dynamodb

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import okio.ByteString
import okio.ByteString.Companion.decodeBase64
import java.nio.ByteBuffer

@Serializable
public data class Identity(
    val principalId: String,
    val type: String,
)

@Serializable
public data class StreamRecord(
    val ApproximateCreationDateTime: Instant,
    val Keys: Map<String, AttributeValue>,
    val NewImage: Map<String, AttributeValue>,
    val OldImage: Map<String, AttributeValue>?,
    val SequenceNumber: String,
    val SizeBytes: Long,
    val StreamViewType: StreamViewType,
)

@Serializable
public data class AttributeValue(
    val S: String?,
    val N: String?,
    val B: ByteBuffer?,
    val SS: List<String>?,
    val NS: List<String>?,
    val BS: List<ByteBuffer>?,
    val M: Map<String, AttributeValue>?,
    val L: List<AttributeValue>?,
    val NULL: Boolean?,
    val BOOL: Boolean?,
)

@Serializer(forClass = InstantSerializer::class)
internal object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("InstantSerializer", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}

@Serializer(forClass = ByteBufferSerializer::class)
internal object ByteBufferSerializer : KSerializer<ByteBuffer> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("ByteBufferSerializer", PrimitiveKind.BYTE)

    override fun serialize(encoder: Encoder, value: ByteBuffer) {
        encoder.encodeString(ByteString.of(*value.array()).base64())
    }

    override fun deserialize(decoder: Decoder): ByteBuffer {
        return ByteBuffer.wrap(decoder.decodeString().decodeBase64()!!.toByteArray())
    }
}