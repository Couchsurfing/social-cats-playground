package com.nicolasmilliard.socialcatsaws.profile.backend.functions

import com.amazonaws.services.lambda.runtime.Client
import com.amazonaws.services.lambda.runtime.ClientContext
import com.amazonaws.services.lambda.runtime.CognitoIdentity
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.LambdaLogger
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.InputStream

class OnDynamoStreamTest {

  private val fakeContext = FakeContext()

  @Test
  fun test() {
    val eventStream: InputStream = this.javaClass.getResourceAsStream("event.json")
    val handler = OnDynamoStream()

    val outputStream = ByteArrayOutputStream()
    val response = handler.handleRequest(eventStream, outputStream,fakeContext)



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
