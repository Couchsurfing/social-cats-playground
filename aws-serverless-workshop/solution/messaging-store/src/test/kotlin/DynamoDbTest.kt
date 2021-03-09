package com.nicolasmilliard.serverlessworkshop.messaging

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.fail
import software.amazon.awssdk.auth.credentials.AwsCredentials
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.net.URI

abstract class DynamoDbTest {
    lateinit var sClient: DynamoDbClient
    lateinit var sServer: DynamoDBProxyServer

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
        createTables()
    }

    abstract fun createTables()

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
}
