package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb

import com.nicolasmilliard.cloudmetric.FakeCloudMetrics
import com.nicolasmilliard.socialcatsaws.profile.model.Device
import com.nicolasmilliard.socialcatsaws.profile.model.DeviceIdProvider
import com.nicolasmilliard.socialcatsaws.profile.model.SupportedPlatform
import com.nicolasmilliard.socialcatsaws.profile.model.User
import kotlinx.datetime.Clock
import software.amazon.awssdk.core.waiters.WaiterResponse
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.BillingMode
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse
import software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.Projection
import software.amazon.awssdk.services.dynamodb.model.ProjectionType
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter

public class UsersDbUtil(private val client: DynamoDbClient) {
  public val usersDb: UsersDynamoDb = UsersDynamoDb(client, Schema.TABLE_NAME, FakeCloudMetrics())

  public fun createTable() {
    val dbWaiter: DynamoDbWaiter = client.waiter()
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
      .globalSecondaryIndexes(
        GlobalSecondaryIndex.builder()
          .indexName(Schema.GSI1_INDEX_NAME)
          .keySchema(
            KeySchemaElement.builder().attributeName(Schema.SharedAttributes.SORT_KEY)
              .keyType(
                KeyType.HASH
              ).build(),
            KeySchemaElement.builder().attributeName(Schema.SharedAttributes.PARTITION_KEY)
              .keyType(
                KeyType.RANGE
              ).build()
          )
          .projection(Projection.builder().projectionType(ProjectionType.KEYS_ONLY).build())
          .build()
      )
      .billingMode(BillingMode.PAY_PER_REQUEST)
      .tableName(Schema.TABLE_NAME)
      .build()

    val response = client.createTable(request)
    val tableRequest: DescribeTableRequest = DescribeTableRequest.builder()
      .tableName(Schema.TABLE_NAME)
      .build()

    // Wait until the Amazon DynamoDB table is created
    val waiterResponse: WaiterResponse<DescribeTableResponse> =
      dbWaiter.waitUntilTableExists(tableRequest)
    waiterResponse.matched().response().ifPresent(System.out::println)
    val newTable = response.tableDescription().tableName()
    println("Created $newTable")
  }

  public fun deleteTables() {
    val dbWaiter: DynamoDbWaiter = client.waiter()
    val request = DeleteTableRequest.builder()
      .tableName(Schema.TABLE_NAME)
      .build()
    val response = client.deleteTable(request)
    val waiterResponse: WaiterResponse<DescribeTableResponse> =
      dbWaiter.waitUntilTableNotExists(
        DescribeTableRequest.builder().tableName(Schema.TABLE_NAME).build()
      )
    waiterResponse.matched().response().ifPresent(System.out::println)
    val deletedTable = response.tableDescription().tableName()
    println("Deleted $deletedTable")
  }

  public fun generateData() {
    usersDb.updateUser(user1)
    usersDb.updateUser(user2)
    usersDb.insertDevice(device1)
    usersDb.insertDevice(device2)
  }

  public val user1: User = User(
    id = "id1",
    createdAt = Clock.System.now(),
    email = "email1@email.com",
    emailVerified = true,
    name = "User Name",
    avatar = null,
    imageCount = 0,
    notificationKey = null
  )
  public val user2: User = User(
    id = "id12",
    createdAt = Clock.System.now(),
    email = "email2@email.com",
    emailVerified = true,
    name = "User Name 2",
    avatar = null,
    imageCount = 0,
    notificationKey = null
  )
  public val device1: Device = Device(
    userId = user1.id,
    instanceId = "InstanceId1",
    createdAt = Clock.System.now(),
    token = "token1",
    provider = DeviceIdProvider.FCM,
    platform = SupportedPlatform.ANDROID,
    appVersionCode = 1,
    languageTag = "en"
  )
  public val device2: Device = Device(
    userId = user1.id,
    instanceId = "InstanceId2",
    createdAt = Clock.System.now(),
    token = "token2",
    provider = DeviceIdProvider.FCM,
    platform = SupportedPlatform.ANDROID,
    appVersionCode = 2,
    languageTag = "fr"
  )
}
