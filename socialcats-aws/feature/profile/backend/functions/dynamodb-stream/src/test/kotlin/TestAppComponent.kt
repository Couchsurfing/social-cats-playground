package com.nicolasmilliard.socialcatsaws.profile.backend.functions

import com.amazonaws.dynamodb.stream.fanout.publisher.EventBridgePublisher
import com.amazonaws.dynamodb.stream.fanout.publisher.EventBridgeRetryClient
import com.amazonaws.dynamodb.stream.fanout.publisher.EventPublisher
import com.amazonaws.dynamodb.stream.fanout.publisher.SqsEventPublisher
import com.nicolasmilliard.di.scope.AppScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.core.SdkSystemSetting
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.core.retry.RetryPolicy
import software.amazon.awssdk.http.SdkHttpClient
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.eventbridge.EventBridgeClient
import software.amazon.awssdk.services.sqs.SqsClient
import java.time.Duration
import javax.inject.Singleton


@Module
@ContributesTo(AppScope::class,
replaces = [AppModule::class])
object TestAppModule {

    @Singleton
    @Provides
    fun provideJson(): Json {
        return Json{}
    }

    @Singleton
    @Provides
    fun provideRegion(): String {
        return System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())
    }

    @Singleton
    @Provides
    fun provideSdkHttpClient(): SdkHttpClient {
        return UrlConnectionHttpClient.builder().build()
    }


    @Provides
    @Singleton
    fun provideEventBridgeClient(region: String): EventBridgeClient {
        // Creating the DynamoDB client followed AWS SDK v2 best practice to improve Lambda performance:
        // https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/client-configuration-starttime.html
        return EventBridgeClient.builder()
            .region(Region.of(region))
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .overrideConfiguration(
                ClientOverrideConfiguration.builder()
                    .apiCallAttemptTimeout(Duration.ofSeconds(1))
                    .retryPolicy(RetryPolicy.builder().numRetries(10).build())
                    .build()
            )
            .httpClientBuilder(UrlConnectionHttpClient.builder())
            .build()
    }

    @Provides
    @Singleton
    fun provideSqsClient(region: String): SqsClient {
        // Creating the DynamoDB client followed AWS SDK v2 best practice to improve Lambda performance:
        // https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/client-configuration-starttime.html
        return SqsClient.builder()
            .region(Region.of(region))
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .httpClientBuilder(UrlConnectionHttpClient.builder())
            .build()
    }

    @Provides
    @Singleton
    fun provideEventPublisher(eventBridge: EventBridgeClient, sqs: SqsClient): EventPublisher {
        val failedEventPublisher: EventPublisher = SqsEventPublisher(sqs, System.getenv("DLQ_URL"))
        val eventBridgeRetryClient = EventBridgeRetryClient(eventBridge, System.getenv("MAX_ATTEMPT").toInt())
        return EventBridgePublisher(
            eventBridgeRetryClient, failedEventPublisher,
            System.getenv("EVENT_BUS_NAME")
        )
    }
}
