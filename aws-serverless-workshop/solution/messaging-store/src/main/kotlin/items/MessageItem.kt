package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb

import com.nicolasmilliard.serverlessworkshop.Schema
import com.nicolasmilliard.serverlessworkshop.messaging.Message
import com.nicolasmilliard.serverlessworkshop.messaging.items.ConversationItem
import kotlinx.datetime.Instant
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags

internal val MESSAGES_TABLE_SCHEMA: TableSchema<MessageItem> = TableSchema.builder(MessageItem::class.java)
    .newItemSupplier { MessageItem() }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.SharedAttributes.PARTITION_KEY)
            .getter(MessageItem::partition_key)
            .setter { obj, v -> obj.partition_key = v }
            .tags(StaticAttributeTags.primaryPartitionKey())
    }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.SharedAttributes.SORT_KEY)
            .getter(MessageItem::sort_key)
            .setter { obj, v -> obj.sort_key = v }
            .tags(StaticAttributeTags.primarySortKey())
    }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.SharedAttributes.ITEM_TYPE)
            .getter(MessageItem::item_type)
            .setter { obj, v -> obj.item_type = v }
    }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.MessageItem.Attributes.MESSAGE_ID)
            .getter(MessageItem::id)
            .setter { obj, v -> obj.id = v }
    }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.MessageItem.Attributes.CREATED_AT)
            .getter { item -> item.createdAt.toString() }
            .setter { obj, v -> obj.createdAt = Instant.parse(v) }
    }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.MessageItem.Attributes.CONTENT)
            .getter(MessageItem::content)
            .setter { obj, v -> obj.content = v }
    }
    .build()

internal data class MessageItem(
    var id: String? = null,
    var conversationId: String? = null,
    var createdAt: Instant? = null,
    var content: String? = null,
) {
    var partition_key
        get() = ConversationItem.key(conversationId)
        set(_) {
            // Ignore needed by enhanced client
        }

    var sort_key
        get() = "${Schema.MessageItem.KEY_PREFIX}$createdAt#$id"
        set(_) {
            // Ignore needed by enhanced client
        }

    var item_type
        get() = Schema.MessageItem.TYPE
        set(_) {
            // Ignore needed by enhanced client
        }
}

internal fun MessageItem.toMessage() = Message(id!!, conversationId!!, createdAt!!, content!!)
internal fun Message.toMessageItem() = MessageItem(id, conversationId, createdAt, content)
