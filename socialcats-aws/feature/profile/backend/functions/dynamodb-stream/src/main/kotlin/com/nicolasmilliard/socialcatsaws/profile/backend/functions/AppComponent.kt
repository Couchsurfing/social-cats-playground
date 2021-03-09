package com.nicolasmilliard.socialcatsaws.profile.backend.functions

import com.nicolasmilliard.di.scope.AppScope
import com.nicolasmilliard.socialcatsaws.eventpublisher.BatchEventPublisher
import com.nicolasmilliard.socialcatsaws.eventpublisher.eventbridge.EventBridgePublisher
import com.nicolasmilliard.socialcatsaws.eventpublisher.eventbridge.EventBridgeRetryClient
import com.nicolasmilliard.socialcatsaws.eventpublisher.sqs.SqsEventPublisher
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.Module
import dagger.Provides
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.core.SdkSystemSetting
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.core.retry.RetryPolicy
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.eventbridge.EventBridgeClient
import software.amazon.awssdk.services.sqs.SqsClient
import java.time.Duration
import javax.inject.Singleton

@MergeComponent(AppScope::class)
@Singleton
interface AppComponent {
  fun getEventBusPublisher(): BatchEventPublisher
}

@Module
@ContributesTo(AppScope::class)
object AppModule {

  @Singleton
  @Provides
  fun provideRegion(): Region {
    return Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable()))
  }

  @Singleton
  @Provides
  fun provideEnvironmentVariableCredentialsProvider(): EnvironmentVariableCredentialsProvider {
    return EnvironmentVariableCredentialsProvider.create()
  }

  @Provides
  @Singleton
  fun provideEventBridgeClient(region: Region, credentialsProvider: EnvironmentVariableCredentialsProvider): EventBridgeClient {
    // Creating the DynamoDB client followed AWS SDK v2 best practice to improve Lambda performance:
    // https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/client-configuration-starttime.html
    return EventBridgeClient.builder()
      .region(region)
      .credentialsProvider(credentialsProvider)
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
  fun provideSqsClient(region: Region, credentialsProvider: EnvironmentVariableCredentialsProvider): SqsClient {
    // Creating the DynamoDB client followed AWS SDK v2 best practice to improve Lambda performance:
    // https://docs.aws.amazon.com/sdk-for-java/v2/developer-guide/client-configuration-starttime.html
    return SqsClient.builder()
      .region(region)
      .credentialsProvider(credentialsProvider)
      .httpClientBuilder(UrlConnectionHttpClient.builder())
      .build()
  }

  @Provides
  @Singleton
  fun provideEventPublisher(eventBridge: EventBridgeClient, sqs: SqsClient): BatchEventPublisher {
    val dlqUrl = System.getenv("DLQ_URL")
    val maxAttempt = System.getenv("MAX_ATTEMPT")
    val eventBusName = System.getenv("EVENT_BUS_NAME")
    val failedEventPublisher: BatchEventPublisher = SqsEventPublisher(sqs, dlqUrl)
    val eventBridgeRetryClient = EventBridgeRetryClient(eventBridge, maxAttempt.toInt())
    return EventBridgePublisher(
      eventBridge = eventBridgeRetryClient,
      failedEventPublisher = failedEventPublisher,
      eventBusName = eventBusName
    )
  }
}
