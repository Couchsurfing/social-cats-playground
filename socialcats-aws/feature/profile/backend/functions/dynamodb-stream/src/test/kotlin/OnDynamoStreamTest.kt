package com.nicolasmilliard.socialcatsaws.profile.backend.functions

import com.amazonaws.services.lambda.runtime.Client
import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import com.amazonaws.services.lambda.runtime.tests.EventLoader
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OnDynamoStreamTest {

  private val fakeContext = FakeContext()

  @Test
  fun testNewImage() {
    val event = EventLoader.loadDynamoDbEvent("dynamodb_new_image_event.json")
    val appComponent: TestAppComponent = DaggerTestAppComponent.create()
    val fakeEventBusPublisher = appComponent.getEventBusPublisher() as FakeEventBusPublisher
    val handler = OnDynamoStream(appComponent)

    handler.handleRequest(event, fakeContext)

    assertEquals(1, fakeEventBusPublisher.published.size)
  }

  @Test
  fun testNewUser() {
    val event = EventLoader.loadDynamoDbEvent("dynamodb_new_user_event.json")
    val appComponent: TestAppComponent = DaggerTestAppComponent.create()
    val fakeEventBusPublisher = appComponent.getEventBusPublisher() as FakeEventBusPublisher
    val handler = OnDynamoStream(appComponent)

    handler.handleRequest(event, fakeContext)

    assertEquals(0, fakeEventBusPublisher.published.size)
  }

  @Test
  fun testRemoveDevice() {
    val event = EventLoader.loadDynamoDbEvent("dynamodb_remove_device_event.json")
    val appComponent: TestAppComponent = DaggerTestAppComponent.create()
    val fakeEventBusPublisher = appComponent.getEventBusPublisher() as FakeEventBusPublisher
    val handler = OnDynamoStream(appComponent)

    handler.handleRequest(event, fakeContext)

    assertEquals(0, fakeEventBusPublisher.published.size)
  }

  class FakeContext : Context {
    override fun getAwsRequestId() = "RequestId"

    override fun getLogGroupName() = "RequestId"

    override fun getLogStreamName() = "RequestId"

    override fun getFunctionName() = "RequestId"

    override fun getFunctionVersion() = "RequestId"

    override fun getInvokedFunctionArn() = "RequestId"

    override fun getIdentity(): CognitoIdentity {
      return object : CognitoIdentity {
        override fun getIdentityId() = "RequestId"

        override fun getIdentityPoolId() = "RequestId"
      }
    }

    override fun getClientContext(): ClientContext {
      return object : ClientContext {
        override fun getClient(): Client {
          return object : Client {
            override fun getInstallationId() = "RequestId"

            override fun getAppTitle() = "RequestId"

            override fun getAppVersionName() = "RequestId"

            override fun getAppVersionCode() = "RequestId"

            override fun getAppPackageName() = "RequestId"
          }
        }

        override fun getCustom(): MutableMap<String, String> {
          return mutableMapOf()
        }

        override fun getEnvironment(): MutableMap<String, String> {
          return mutableMapOf()
        }
      }
    }

    override fun getRemainingTimeInMillis() = 10000

    override fun getMemoryLimitInMB() = 512

    override fun getLogger(): LambdaLogger {
      return object : LambdaLogger {
        override fun log(message: String?) {
          print(message)
        }

        override fun log(message: ByteArray?) {
          print(message)
        }
      }
    }
  }
}
