package com.nicolasmilliard.serverlessworkshop.messaging

import com.nicolasmilliard.serverlessworkshop.Schema
import kotlinx.datetime.Clock
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import software.amazon.awssdk.core.waiters.WaiterResponse
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.BillingMode
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.CreateTableResponse
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter
import kotlin.time.minutes

class MessageStoreTest : DynamoDbTest() {

    @Test
    fun testAddConversation() {
        val messagingStore = MessagingStore(sClient)

        val conversation1 = Conversation("id", "Conversation 1")
        messagingStore.addConversation(conversation1)

        val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
        Assertions.assertEquals(1, scanResult.count())
        val item = scanResult.items()[0]
        Assertions.assertEquals(
            Schema.ConversationItem.KEY_PREFIX + conversation1.id,
            item.getValue(Schema.SharedAttributes.PARTITION_KEY).s()
        )
        Assertions.assertEquals(
            Schema.ConversationItem.KEY_PREFIX + conversation1.id,
            item.getValue(Schema.SharedAttributes.SORT_KEY).s()
        )
        Assertions.assertEquals(conversation1.name, item.getValue(Schema.ConversationItem.Attributes.NAME).s())
    }

    @Test
    fun testAddMessage() {
        val messagingStore = MessagingStore(sClient)

        val conversation1 = Conversation("id", "Conversation 1")
        messagingStore.addConversation(conversation1)

        val message1 = Message("id", conversation1.id, Clock.System.now(), "Hello")
        messagingStore.addMessage(message1)
        val message2 = Message("id2", conversation1.id, Clock.System.now().plus(30.minutes), "World")
        messagingStore.addMessage(message2)

        val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
        Assertions.assertEquals(3, scanResult.count())
        val convItem = scanResult.items()[0]
        Assertions.assertEquals(
            Schema.ConversationItem.KEY_PREFIX + conversation1.id,
            convItem.getValue(Schema.SharedAttributes.PARTITION_KEY).s()
        )
        val message1Item = scanResult.items()[1]
        Assertions.assertEquals(
            Schema.ConversationItem.KEY_PREFIX + conversation1.id,
            message1Item.getValue(Schema.SharedAttributes.PARTITION_KEY).s()
        )
        Assertions.assertEquals(
            "${Schema.MessageItem.KEY_PREFIX}${message1.createdAt}#${message1.id}",
            message1Item.getValue(Schema.SharedAttributes.SORT_KEY).s()
        )
        Assertions.assertEquals(
            message1.content,
            message1Item.getValue(Schema.MessageItem.Attributes.CONTENT).s()
        )
        val message2Item = scanResult.items()[2]
        Assertions.assertEquals(
            Schema.ConversationItem.KEY_PREFIX + conversation1.id,
            message2Item.getValue(Schema.SharedAttributes.PARTITION_KEY).s()
        )
        Assertions.assertEquals(
            "${Schema.MessageItem.KEY_PREFIX}${message2.createdAt}#${message2.id}",
            message2Item.getValue(Schema.SharedAttributes.SORT_KEY).s()
        )
        Assertions.assertEquals(
            message2.content,
            message2Item.getValue(Schema.MessageItem.Attributes.CONTENT).s()
        )
    }

    override fun createTables() {
        val dbWaiter: DynamoDbWaiter = sClient.waiter()
        val request: CreateTableRequest = CreateTableRequest.builder()
            .attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName(Schema.SharedAttributes.PARTITION_KEY)
                    .attributeType(ScalarAttributeType.S)
                    .build(),
                AttributeDefinition.builder()
                    .attributeName(Schema.SharedAttributes.SORT_KEY)
                    .attributeType(ScalarAttributeType.S)
                    .build(),
            )
            .keySchema(
                KeySchemaElement.builder()
                    .attributeName(Schema.SharedAttributes.PARTITION_KEY)
                    .keyType(KeyType.HASH)
                    .build(),
                KeySchemaElement.builder()
                    .attributeName(Schema.SharedAttributes.SORT_KEY)
                    .keyType(KeyType.RANGE)
                    .build(),
            )
//            .globalSecondaryIndexes(
//                GlobalSecondaryIndex.builder()
//                    .indexName(Schema.GSI1_INDEX_NAME)
//                    .keySchema(
//                        KeySchemaElement.builder().attributeName(Schema.SharedAttributes.SORT_KEY).keyType(KeyType.HASH).build(),
//                        KeySchemaElement.builder().attributeName(Schema.SharedAttributes.PARTITION_KEY).keyType(KeyType.RANGE).build()
//                    )
//                    .projection(Projection.builder().projectionType(ProjectionType.KEYS_ONLY).build())
//                    .build()
//            )
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .tableName(Schema.TABLE_NAME)
            .build()

        try {
            val response: CreateTableResponse = sClient.createTable(request)
            val tableRequest: DescribeTableRequest = DescribeTableRequest.builder()
                .tableName(Schema.TABLE_NAME)
                .build()

            // Wait until the Amazon DynamoDB table is created
            val waiterResponse: WaiterResponse<DescribeTableResponse> =
                dbWaiter.waitUntilTableExists(tableRequest)
            waiterResponse.matched().response().ifPresent(System.out::println)
            val newTable = response.tableDescription().tableName()
            println("Created $newTable")
        } catch (e: DynamoDbException) {
            fail(e)
        }
    }
}
