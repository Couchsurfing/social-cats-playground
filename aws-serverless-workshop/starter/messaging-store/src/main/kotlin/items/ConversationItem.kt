package com.nicolasmilliard.serverlessworkshop.messaging.items

import com.nicolasmilliard.serverlessworkshop.Schema
import com.nicolasmilliard.serverlessworkshop.messaging.Conversation
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags

internal val CONVERSATIONS_TABLE_SCHEMA: TableSchema<ConversationItem> = TableSchema.builder(ConversationItem::class.java)
    .newItemSupplier { ConversationItem() }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.SharedAttributes.PARTITION_KEY)
            .getter(ConversationItem::partition_key)
            .setter { obj, v -> obj.partition_key = v }
            .tags(StaticAttributeTags.primaryPartitionKey())
    }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.SharedAttributes.SORT_KEY)
            .getter(ConversationItem::sort_key)
            .setter { obj, v -> obj.sort_key = v }
            .tags(StaticAttributeTags.primarySortKey())
    }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.SharedAttributes.ITEM_TYPE)
            .getter(ConversationItem::item_type)
            .setter { obj, v -> obj.item_type = v }
    }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.ConversationItem.Attributes.CONVERSATION_ID)
            .getter(ConversationItem::id)
            .setter { obj, v -> obj.id = v }
    }
    .addAttribute(
        String::class.java
    ) {
        it.name(Schema.ConversationItem.Attributes.NAME)
            .getter(ConversationItem::name)
            .setter { obj, v -> obj.name = v }
    }
    .build()

internal data class ConversationItem(
    var id: String? = null,
    var name: String? = null,
) {
    var partition_key
        get() = key(id)
        set(_) {
            // Ignore needed by enhanced client
        }

    var sort_key
        get() = key(id)
        set(_) {
            // Ignore needed by enhanced client
        }

    var item_type
        get() = Schema.ConversationItem.TYPE
        set(_) {
            // Ignore needed by enhanced client
        }

    companion object {
        fun key(id: String?): String {
            return "${Schema.ConversationItem.KEY_PREFIX}$id"
        }
    }
}

internal fun ConversationItem.toConversation() =
    Conversation(id!!, name!!)

internal fun Conversation.toConversationItem() =
    ConversationItem(id, name)
