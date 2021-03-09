package com.nicolasmilliard.serverlessworkshop.messaging

import com.nicolasmilliard.serverlessworkshop.Schema
import com.nicolasmilliard.serverlessworkshop.messaging.items.CONVERSATIONS_TABLE_SCHEMA
import com.nicolasmilliard.serverlessworkshop.messaging.items.ConversationItem
import com.nicolasmilliard.serverlessworkshop.messaging.items.toConversation
import com.nicolasmilliard.serverlessworkshop.messaging.items.toConversationItem
import com.nicolasmilliard.serverlessworkshop.messaging.pagination.DynamoDbStartKeySerializer
import com.nicolasmilliard.serverlessworkshop.messaging.pagination.TokenSerializer
import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.MESSAGES_TABLE_SCHEMA
import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.toMessage
import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.toMessageItem
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.Key
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class MessagingStore(
    client: DynamoDbClient
) {

    private val paginationTokenSerializer: TokenSerializer<Map<String, AttributeValue>> = DynamoDbStartKeySerializer()
    private val enhancedClient = DynamoDbEnhancedClient.builder()
        .dynamoDbClient(client)
        .build()

    private val conversationsTableSchema = CONVERSATIONS_TABLE_SCHEMA
    private val conversationsTable = enhancedClient.table(Schema.TABLE_NAME, conversationsTableSchema)

    private val messagesTableSchema = MESSAGES_TABLE_SCHEMA
    private val messagesTable = enhancedClient.table(Schema.TABLE_NAME, messagesTableSchema)

    fun getConversations(limit: Int, pageToken: String?): ConversationsPage {
        val builder = QueryEnhancedRequest.builder()
            .limit(limit)
        if (pageToken != null) {
            builder.exclusiveStartKey(paginationTokenSerializer.deserialize(pageToken))
        }
        val response = conversationsTable.query(builder.build())
        val conversations = response.items().asSequence().map { it.toConversation() }.toList()
        var lastEvaluatedKey: String? = getNextPageToken(response)
        return ConversationsPage(conversations, lastEvaluatedKey)
    }

    fun getConversationWithMessages(conversationId: String, limit: Int, pageToken: String?): ConversationWithMessages {

        val conversation = getConversation(conversationId)
        val messagesPage = getMessages(conversationId, limit, pageToken)

        return ConversationWithMessages(conversation, messagesPage)
    }

    fun getMessages(conversationId: String, limit: Int, pageToken: String?): MessagesPage {
        val builder = QueryEnhancedRequest.builder()
            .queryConditional(QueryConditional.keyEqualTo(Key.builder().partitionValue(ConversationItem.key(conversationId)).build()))
            .limit(limit)
        if (pageToken != null) {
            builder.exclusiveStartKey(paginationTokenSerializer.deserialize(pageToken))
        }
        val response = messagesTable.query(builder.build())
        val messages = response.items().asSequence().map { it.toMessage() }.toList()
        var nextPageToken = getNextPageToken(response)
        return MessagesPage(messages, nextPageToken)
    }

    fun getConversation(conversationId: String): Conversation {
        return conversationsTable.getItem(Key.builder().partitionValue(ConversationItem.key(conversationId)).build()).toConversation()
    }

    fun addConversation(conversation: Conversation) {
        conversationsTable.putItem(conversation.toConversationItem())
    }

    fun addMessage(message: Message) {
        messagesTable.putItem(message.toMessageItem())
    }

    private fun <T> getNextPageToken(paged: PageIterable<T>): String? {
        val iter = paged.iterator()
        while (iter.hasNext()) {
            val next = iter.next()
            if (!iter.hasNext() && next.lastEvaluatedKey() != null) {
                return paginationTokenSerializer.serialize(next.lastEvaluatedKey())
            }
        }
        return null
    }
}
