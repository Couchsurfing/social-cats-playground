@file:UseSerializers(InstantSerializer::class)
package com.nicolasmilliard.serverlessworkshop.messaging

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Conversation(
    val id: String,
    val name: String,
)

@Serializable
data class Message(
    val id: String,
    val conversationId: String,
    var createdAt: Instant,
    val content: String,
)

@Serializable
data class ConversationWithMessages(
    val conversation: Conversation,
    val messagesPage: MessagesPage
)
@Serializable
data class ConversationsPage(
    val conversations: List<Conversation>,
    val nextPageToken: String?
)

@Serializable
data class MessagesPage(
    val messages: List<Message>,
    val nextPageToken: String?
)

object InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        val string = decoder.decodeString()
        return Instant.parse(string)
    }
}
