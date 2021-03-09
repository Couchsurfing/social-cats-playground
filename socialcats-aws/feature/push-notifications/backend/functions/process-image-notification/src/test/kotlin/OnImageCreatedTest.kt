package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.UsersDbUtil
import org.junit.AfterClass
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.sqs.SqsClient
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest
import java.io.ByteArrayOutputStream
import org.junit.jupiter.api.Assertions.assertEquals
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest

@Testcontainers
class OnImageCreatedTest {

    private val fakeContext = FakeContext()

    var localstackImage = DockerImageName.parse("localstack/localstack:0.12.7")

    @Container
    var localstack = LocalStackContainer(localstackImage)
        .withServices(
            LocalStackContainer.Service.SQS,
            LocalStackContainer.Service.DYNAMODB
        )

    lateinit var sqsClient: SqsClient
    lateinit var queueUrl: String
    lateinit var usersDbUtil: UsersDbUtil

    @BeforeEach
    fun beforeEach() {
        sqsClient = createSqsClient()
        queueUrl = sqsClient.createQueue(
            CreateQueueRequest.builder()
                .queueName("TestQueue")
                .build()
        ).queueUrl()

        val createDynamoDbClient = createDynamoDbClient()
        usersDbUtil = UsersDbUtil(createDynamoDbClient)
        usersDbUtil.createTable()
    }



    @AfterEach
    fun afterEach() {
        sqsClient.deleteQueue { it.queueUrl(queueUrl) }
        usersDbUtil.deleteTables()
    }

    @Test
    fun testNewImageNoDevice() {

        val appComponent: AppComponent = DaggerTestAppComponent.builder()
            .localContainer(localstack)
            .queueUrl(queueUrl)
            .build()

        val handler = OnImageCreated(appComponent)
        val output = ByteArrayOutputStream()
        handler.handleRequest(
            this::class.java.classLoader.getResourceAsStream("awsevent.json")!!,
            output,
            fakeContext
        )

        val messages =
            sqsClient.receiveMessage(ReceiveMessageRequest.builder().queueUrl(queueUrl).build())
                .messages()

        assertEquals(0, messages.size)
    }

    @Test
    fun testNewImageWithDevices() {
        usersDbUtil.generateData()
        val appComponent: AppComponent = DaggerTestAppComponent.builder()
            .localContainer(localstack)
            .queueUrl(queueUrl)
            .build()

        val handler = OnImageCreated(appComponent)
        val output = ByteArrayOutputStream()
        handler.handleRequest(
            this::class.java.classLoader.getResourceAsStream("awsevent.json")!!,
            output,
            fakeContext
        )

        val messages =
            sqsClient.receiveMessage(ReceiveMessageRequest.builder().queueUrl(queueUrl).build())
                .messages()

        assertEquals(1, messages.size)
    }

    private fun createSqsClient(): SqsClient {
        return SqsClient
            .builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.SQS))
            .httpClient(UrlConnectionHttpClient.builder().build())
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        localstack.accessKey, localstack.secretKey
                    )
                )
            )
            .region(Region.of(localstack.region))
            .build()
    }

    private fun createDynamoDbClient(): DynamoDbClient {
        return DynamoDbClient.builder()
            .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB))
            .httpClient(UrlConnectionHttpClient.builder().build())
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        localstack.accessKey, localstack.secretKey
                    )
                )
            )
            .region(Region.of(localstack.region))
            .build()
    }
}