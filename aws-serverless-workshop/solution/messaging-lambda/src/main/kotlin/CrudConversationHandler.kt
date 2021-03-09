package com.nicolasmilliard.serverlessworkshop.messaging

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestHandler
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.core.SdkSystemSetting
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

class CrudConversationHandler: RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {
    private val store: MessagingStore
    private val json = Json {  }
    init {
        store = MessagingStore(createDynamoDbClient())
    }
    override fun handleRequest(input: APIGatewayV2HTTPEvent, context: Context): APIGatewayV2HTTPResponse {
        when(input.requestContext.http.method){
            "POST" ->{
                val conversation: Conversation = json.decodeFromString(input.body)
                store.addConversation(conversation)
                return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(201)
                    .withHeaders(mapOf("Content-Type" to "application/json"))
                    .withBody(input.body)
                    .build()
            }
            else -> throw UnsupportedOperationException("Unsupported HTTP method")
        }
    }

    private fun createDynamoDbClient(): DynamoDbClient {
        val region = System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())
        val httpClient = UrlConnectionHttpClient.builder().build()
        return DynamoDbClient.builder()
            .httpClient(httpClient)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .region(Region.of(region))
            .overrideConfiguration(ClientOverrideConfiguration.builder().build())
            .endpointOverride(URI("https://dynamodb.$region.amazonaws.com"))
            .build()
    }
}