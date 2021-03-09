package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer
import com.nicolasmilliard.cloudmetric.FakeCloudMetrics
import com.nicolasmilliard.socialcatsaws.profile.model.Avatar
import com.nicolasmilliard.socialcatsaws.profile.model.Device
import com.nicolasmilliard.socialcatsaws.profile.model.DeviceIdProvider
import com.nicolasmilliard.socialcatsaws.profile.model.Image
import com.nicolasmilliard.socialcatsaws.profile.model.SupportedPlatform
import com.nicolasmilliard.socialcatsaws.profile.model.User
import com.nicolasmilliard.socialcatsaws.profile.repository.InsertResult
import kotlinx.datetime.Clock
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.net.URI

internal class UsersDynamodDbTest {
  lateinit var sClient: DynamoDbClient
  lateinit var sServer: DynamoDBProxyServer
  lateinit var usersDbUtil: UsersDbUtil

  @BeforeEach
  fun beforeEach() {
    System.setProperty("sqlite4java.library.path", "./build/libs/")

    // Create an in-memory and in-process instance of DynamoDB Local that runs over HTTP
    val localArgs = arrayOf("-inMemory")

    try {
      sServer = ServerRunner.createServerFromCommandLineArgs(localArgs)
      sServer.safeStart()
    } catch (e: Exception) {
      fail(e.message)
    }
    createAmazonDynamoDBClient()

    usersDbUtil = UsersDbUtil(sClient)
    usersDbUtil.createTable()

    val scanResultBefore = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
    assertEquals(0, scanResultBefore.count())
  }

  @AfterEach
  fun afterEach() {
    // deleteTables()
    try {
      sServer.stop()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun createAmazonDynamoDBClient() {
    sClient = DynamoDbClient.builder()
      .httpClient(UrlConnectionHttpClient.builder().build())
      .region(Region.of("us-west-2"))
      .endpointOverride(URI("http://localhost:8000"))
      .credentialsProvider {
        object : AwsCredentials {
          override fun accessKeyId() = "ACCESS-KEY"
          override fun secretAccessKey() = "SECRET_KEY"
        }
      }
      .build()
  }

  @Test
  fun testUpdateUser() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())
    val user = User("id", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
    usersDynamoDb.updateUser(user)

    val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
    assertEquals(1, scanResult.count())
    val items = scanResult.items()
    checkUserItem(user, items[0])
  }

  @Test
  fun testInsertUserAlreadyExist() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())
    val user = User("id", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
    usersDynamoDb.updateUser(user)
    usersDynamoDb.updateUser(user)

    val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
    assertEquals(1, scanResult.count())
    val items = scanResult.items()
    checkUserItem(user, items[0])
  }

  @Test
  fun testInsertImage() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())
    val user = User("id", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
    usersDynamoDb.updateUser(user)

    val image = Image("id", "id", Clock.System.now())
    usersDynamoDb.insertImage(image)

    val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
    assertEquals(2, scanResult.count())
    val item = scanResult.items()[1]
    assertEquals(1, item.getValue(Schema.UserItem.Attributes.IMAGE_COUNT).n().toInt())

    val imageItem = scanResult.items()[0]
    assertEquals(Schema.UserItem.KEY_PREFIX + image.userId, imageItem.getValue(Schema.SharedAttributes.PARTITION_KEY).s())
    assertEquals("${Schema.ImageItem.KEY_PREFIX}${image.createdAt}#${image.id}", imageItem.getValue(Schema.SharedAttributes.SORT_KEY).s())
    assertEquals(image.id, imageItem.getValue(Schema.ImageItem.Attributes.IMAGE_ID).s())
    assertEquals(image.userId, imageItem.getValue(Schema.ImageItem.Attributes.USER_ID).s())
  }

  @Test
  fun testInsertImageNoPriorUser() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())

    val image = Image("id", "id", Clock.System.now())
    usersDynamoDb.insertImage(image)

    val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
    assertEquals(2, scanResult.count())
    val item = scanResult.items()[1]
    assertEquals(Schema.UserItem.KEY_PREFIX + image.userId, item.getValue(Schema.SharedAttributes.PARTITION_KEY).s())
    assertEquals(Schema.UserItem.KEY_PREFIX + image.userId, item.getValue(Schema.SharedAttributes.SORT_KEY).s())
    assertEquals(1, item.getValue(Schema.UserItem.Attributes.IMAGE_COUNT).n().toInt())

    val imageItem = scanResult.items()[0]
    assertEquals(image.id, imageItem.getValue(Schema.ImageItem.Attributes.IMAGE_ID).s())
    assertEquals(image.userId, imageItem.getValue(Schema.ImageItem.Attributes.USER_ID).s())
  }

  @Test
  fun testInsertImageAlreadyExisting() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())

    val image = Image("id", "id", Clock.System.now())
    val insertImage = usersDynamoDb.insertImage(image)
    assertEquals(InsertResult.Added, insertImage)

    val insertImage1 = usersDynamoDb.insertImage(image)
    assertEquals(InsertResult.AlreadyExist, insertImage1)

    val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
    assertEquals(2, scanResult.count())
    val item = scanResult.items()[1]
    assertEquals(Schema.UserItem.KEY_PREFIX + image.userId, item.getValue(Schema.SharedAttributes.PARTITION_KEY).s())
    assertEquals(Schema.UserItem.KEY_PREFIX + image.userId, item.getValue(Schema.SharedAttributes.SORT_KEY).s())
    assertEquals(1, item.getValue(Schema.UserItem.Attributes.IMAGE_COUNT).n().toInt())

    val imageItem = scanResult.items()[0]
    assertEquals(image.id, imageItem.getValue(Schema.ImageItem.Attributes.IMAGE_ID).s())
    assertEquals(image.userId, imageItem.getValue(Schema.ImageItem.Attributes.USER_ID).s())
  }

  @Test
  fun testInsertImageAndThenUpdateUser() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())

    val image = Image("id", "id", Clock.System.now())
    usersDynamoDb.insertImage(image)

    val user = User("id", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
    usersDynamoDb.updateUser(user)

    val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
    assertEquals(2, scanResult.count())
    val item = scanResult.items()[1]
    assertEquals(Schema.UserItem.KEY_PREFIX + user.id, item.getValue(Schema.SharedAttributes.PARTITION_KEY).s())
    assertEquals(Schema.UserItem.KEY_PREFIX + image.userId, item.getValue(Schema.SharedAttributes.SORT_KEY).s())
    assertEquals(Schema.UserItem.TYPE, item.getValue(Schema.SharedAttributes.ITEM_TYPE).s())
    assertEquals(user.id, item.getValue(Schema.UserItem.Attributes.USER_ID).s())
    assertEquals(user.email, item.getValue(Schema.UserItem.Attributes.EMAIL).s())
    assertEquals(user.emailVerified, item.getValue(Schema.UserItem.Attributes.EMAIL_VERIFIED).bool())
    assertEquals(user.name, item.getValue(Schema.UserItem.Attributes.NAME).s())
    assertEquals(user.avatar!!.imageId, item.getValue(Schema.UserItem.Attributes.AVATAR_IMAGE_ID).s())
    assertEquals(1, item.getValue(Schema.UserItem.Attributes.IMAGE_COUNT).n().toInt())

    val imageItem = scanResult.items()[0]
    assertEquals(image.id, imageItem.getValue(Schema.ImageItem.Attributes.IMAGE_ID).s())
    assertEquals(image.userId, imageItem.getValue(Schema.ImageItem.Attributes.USER_ID).s())
  }

  @Test
  fun testGetUserWithImages() {
    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())

    for (i in 1..3) {
      val userId = "userId$i"
      val user = User(userId, Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
      usersDynamoDb.updateUser(user)

      for (i in 1..5) {
        val image = Image("id$i", userId, Clock.System.now())
        usersDynamoDb.insertImage(image)
      }
    }

    val userAndImages = usersDynamoDb.getUserAndImages("userId2", 3)
    assertNotNull(userAndImages.user)
    assertEquals(5, userAndImages.user!!.imageCount)
    assertEquals(3, userAndImages.images.size)
  }

  @Test
  fun testAddDevice() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())
    val user = User("id", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
    usersDynamoDb.updateUser(user)

    val device = Device(
      user.id,
      "InstanceId",
      Clock.System.now(),
      "token",
      DeviceIdProvider.FCM,
      SupportedPlatform.ANDROID,
      12,
      "fr"
    )

    usersDynamoDb.insertDevice(device)
    val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }

    assertEquals(2, scanResult.count())
    val items = scanResult.items()
    if (items[0].getValue(Schema.SharedAttributes.SORT_KEY).s().startsWith(Schema.DeviceItem.KEY_PREFIX)) {
      checkUserItem(user, items[1])
      checkDeviceItem(device, items[0])
    } else {
      checkUserItem(user, items[0])
      checkDeviceItem(device, items[1])
    }
  }

  @Test
  fun testAddDeviceAlreadyExist() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())
    val user = User("id", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
    usersDynamoDb.updateUser(user)

    val device = Device(
      user.id,
      "InstanceId",
      Clock.System.now(),
      "token",
      DeviceIdProvider.FCM,
      SupportedPlatform.ANDROID,
      12,
      "fr"
    )
    usersDynamoDb.insertDevice(device)
    usersDynamoDb.insertDevice(device)

    val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
    assertEquals(2, scanResult.count())
    val items = scanResult.items()
    if (items[0].getValue(Schema.SharedAttributes.SORT_KEY).s().startsWith(Schema.DeviceItem.KEY_PREFIX)) {
      checkUserItem(user, items[1])
      checkDeviceItem(device, items[0])
    } else {
      checkUserItem(user, items[0])
      checkDeviceItem(device, items[1])
    }
  }

  @Test
  fun testAddDeviceAlreadyExistFoAnotherUser() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())
    val user = User("id", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
    usersDynamoDb.updateUser(user)
    val user2 = User("id2", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
    usersDynamoDb.updateUser(user2)

    val device = Device(
      user.id,
      "InstanceId",
      Clock.System.now(),
      "token",
      DeviceIdProvider.FCM,
      SupportedPlatform.ANDROID,
      12,
      "fr"
    )
    usersDynamoDb.insertDevice(device)

    val device2 = Device(
      user2.id,
      "InstanceId",
      Clock.System.now(),
      "token",
      DeviceIdProvider.FCM,
      SupportedPlatform.ANDROID,
      12,
      "en"
    )
    usersDynamoDb.insertDevice(device2)

    val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
    assertEquals(3, scanResult.count())
    val items = scanResult.items()

    checkUserItem(user, items[0])
    checkUserItem(user2, items[1])
    checkDeviceItem(device2, items[2])
  }

  @Test
  fun testGetDeviceTokens() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())
    val user = User("id", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
    usersDynamoDb.updateUser(user)
    val user2 = User("id2", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0)
    usersDynamoDb.updateUser(user2)

    val device = Device(
      user.id,
      "InstanceId",
      Clock.System.now(),
      "token",
      DeviceIdProvider.FCM,
      SupportedPlatform.ANDROID,
      12,
      "fr"
    )
    usersDynamoDb.insertDevice(device)

    val device2 = Device(
      user2.id,
      "InstanceId",
      Clock.System.now(),
      "token2",
      DeviceIdProvider.FCM,
      SupportedPlatform.ANDROID,
      12,
      "en"
    )
    usersDynamoDb.insertDevice(device2)
    val device3 = Device(
      user2.id,
      "InstanceId",
      Clock.System.now(),
      "token3",
      DeviceIdProvider.FCM,
      SupportedPlatform.ANDROID,
      12,
      "en"
    )
    usersDynamoDb.insertDevice(device3)

    val result = usersDynamoDb.getDeviceTokens(user2.id, 5, null)
    assertNull(result.nextPageToken)
    assertEquals(2, result.tokens.size)
    assertEquals("token2", result.tokens[0])
    assertEquals("token3", result.tokens[1])
  }

  @Test
  fun testUpdateNotificationKey() {

    val usersDynamoDb = UsersDynamoDb(sClient, Schema.TABLE_NAME, FakeCloudMetrics())
    val user = User("id", Clock.System.now(), "email", true, "Name", Avatar("imageId"), 0, null)
    usersDynamoDb.updateUser(user)

    val notifKey = "NotificationKey"
    usersDynamoDb.updateNotificationKey(user.id, notifKey)

    val scanResult = sClient.scan { it.tableName(Schema.TABLE_NAME).build() }
    assertEquals(1, scanResult.count())
    val items = scanResult.items()
    checkUserItem(user.copy(notificationKey = notifKey), items[0])
  }

  private fun checkUserItem(user: User, item: Map<String, AttributeValue>) {
    assertEquals(Schema.UserItem.KEY_PREFIX + user.id, item.getValue(Schema.SharedAttributes.PARTITION_KEY).s())
    assertEquals(Schema.UserItem.KEY_PREFIX + user.id, item.getValue(Schema.SharedAttributes.SORT_KEY).s())
    assertEquals(Schema.UserItem.TYPE, item.getValue(Schema.SharedAttributes.ITEM_TYPE).s())
    assertEquals(user.id, item.getValue(Schema.UserItem.Attributes.USER_ID).s())
    assertEquals(user.createdAt.toString(), item.getValue(Schema.UserItem.Attributes.CREATED_AT).s())
    assertEquals(user.email, item.getValue(Schema.UserItem.Attributes.EMAIL).s())
    assertEquals(user.emailVerified, item.getValue(Schema.UserItem.Attributes.EMAIL_VERIFIED).bool())

    if (user.name == null) {
      assertFalse(item.containsKey(Schema.UserItem.Attributes.NAME))
    } else {
      assertEquals(user.name, item.getValue(Schema.UserItem.Attributes.NAME).s())
    }
    if (user.avatar == null) {
      assertFalse(item.containsKey(Schema.UserItem.Attributes.AVATAR_IMAGE_ID))
    } else {
      assertEquals(user.avatar!!.imageId, item.getValue(Schema.UserItem.Attributes.AVATAR_IMAGE_ID).s())
    }

    assertEquals(user.imageCount, item.getValue(Schema.UserItem.Attributes.IMAGE_COUNT).n().toInt())
    if (user.notificationKey == null) {
      assertFalse(item.containsKey(Schema.UserItem.Attributes.NOTIFICATION_KEY))
    } else {
      assertEquals(user.notificationKey, item.getValue(Schema.UserItem.Attributes.NOTIFICATION_KEY).s())
    }
  }

  private fun checkDeviceItem(device: Device, item: Map<String, AttributeValue>) {
    assertEquals("${Schema.DeviceItem.KEY_PREFIX}${device.token}", item.getValue(Schema.SharedAttributes.PARTITION_KEY).s())
    assertEquals("${Schema.UserItem.KEY_PREFIX}${device.userId}", item.getValue(Schema.SharedAttributes.SORT_KEY).s())
    assertEquals(Schema.DeviceItem.TYPE, item.getValue(Schema.SharedAttributes.ITEM_TYPE).s())
    assertEquals(device.instanceId, item.getValue(Schema.DeviceItem.Attributes.INSTANCE_ID).s())
    assertEquals(device.createdAt.toString(), item.getValue(Schema.DeviceItem.Attributes.CREATED_AT).s())
    assertEquals(device.token, item.getValue(Schema.DeviceItem.Attributes.TOKEN).s())
    assertEquals(device.platform.toString(), item.getValue(Schema.DeviceItem.Attributes.PLATFORM).s())
    assertEquals(device.provider.toString(), item.getValue(Schema.DeviceItem.Attributes.PROVIDER).s())
    assertEquals(device.languageTag, item.getValue(Schema.DeviceItem.Attributes.LANGUAGE_TAG).s())
    assertEquals(device.appVersionCode, item.getValue(Schema.DeviceItem.Attributes.APP_VERSION_CODE).n().toInt())
  }
}
