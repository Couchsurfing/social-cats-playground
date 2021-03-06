package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.amazonaws.xray.interceptors.TracingInterceptor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nicolasmilliard.cloudmetric.CloudMetricModule
import com.nicolasmilliard.cloudmetric.CloudMetrics
import com.nicolasmilliard.di.scope.AppScope
import com.nicolasmilliard.socialcatsaws.eventpublisher.BatchEventPublisher
import com.nicolasmilliard.socialcatsaws.eventpublisher.sqs.SqsEventPublisher
import com.nicolasmilliard.socialcatsaws.profile.repository.DynamoDbTableName
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.Module
import dagger.Provides
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.core.SdkSystemSetting
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.http.SdkHttpClient
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.sqs.SqsClient
import java.net.URI
import javax.inject.Qualifier
import javax.inject.Singleton

@MergeComponent(AppScope::class)
@Singleton
interface AppComponent {
    fun getCloudMetrics(): CloudMetrics
    fun getImageNotificationUseCase(): ImageNotificationUseCase
    fun getObjectMapper(): ObjectMapper
}

@Module
@ContributesTo(AppScope::class)
object ConfigModule {

    @Singleton
    @Provides
    fun provideRegion(): Region {
        return Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable()))
    }

    @Singleton
    @Provides
    fun provideCredentialsProvider(): AwsCredentialsProvider {
        return EnvironmentVariableCredentialsProvider.create()
    }

    @DynamoDbTableName
    @Provides
    fun provideDynamoDbTableName(): String {
        return System.getenv("DDB_TABLE_NAME")
    }

    @DynamoDbEndpoint
    @Provides
    fun provideDynamoDbEndpoint(region: Region): URI {
        return URI("https://dynamodb.$region.amazonaws.com")
    }

    @SqsEndpoint
    @Provides
    fun provideSqsEndpoint(region: Region): URI {
        return URI("https://sqs.$region.amazonaws.com")
    }

    @Provides
    @EventDestinationEndpoint
    fun provideEventPublisherDestination(): String {
        return System.getenv("DESTINATION_QUEUE_URL")
    }

    @Singleton
    @Provides
    fun provideCloudMetrics(): CloudMetrics {
        return CloudMetricModule.provideCloudMetrics(System.getenv("APP_NAME"))
    }
}

@Module
@ContributesTo(AppScope::class)
object AppModule {

    @Singleton
    @Provides
    fun provideObjectMapper(): ObjectMapper {
        return jacksonObjectMapper()
    }

    @Singleton
    @Provides
    fun provideSdkHttpClient(): SdkHttpClient {
        return UrlConnectionHttpClient.builder().build()
    }

    @Singleton
    @Provides
    fun provideDynamoDbClient(
        region: Region, httpClient:
        SdkHttpClient,
        credentialsProvider: AwsCredentialsProvider,
        @DynamoDbEndpoint endpoint: URI
    ): DynamoDbClient {
        return DynamoDbClient.builder()
            .httpClient(httpClient)
            .credentialsProvider(credentialsProvider)
            .region(region)
            .overrideConfiguration(ClientOverrideConfiguration.builder()
                .addExecutionInterceptor(TracingInterceptor()).build())
            .endpointOverride(endpoint)
            .build()
    }

    @Provides
    @Singleton
    fun provideSqsClient(
        region: Region,
        credentialsProvider: AwsCredentialsProvider,
        @SqsEndpoint sqsEndpoint: URI
    ): SqsClient {
        // Creating the DynamoDB client followed AWS SDK v2 best practice to improve Lambda performance:
        // https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/client-configuration-starttime.html
        return SqsClient.builder()
            .region(region)
            .credentialsProvider(credentialsProvider)
            .httpClientBuilder(UrlConnectionHttpClient.builder())
            .overrideConfiguration(ClientOverrideConfiguration.builder()
                .addExecutionInterceptor(TracingInterceptor()).build())
            .endpointOverride(sqsEndpoint)
            .build()
    }

    @Provides
    @Singleton
    fun provideEventPublisher(sqs: SqsClient, @EventDestinationEndpoint destination: String): BatchEventPublisher {
        return SqsEventPublisher(sqs, destination)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class SqsEndpoint

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DynamoDbEndpoint

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EventDestinationEndpoint