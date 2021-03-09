package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.nicolasmilliard.cloudmetric.CloudMetrics
import com.nicolasmilliard.cloudmetric.FakeCloudMetrics
import com.nicolasmilliard.di.scope.AppScope
import com.nicolasmilliard.socialcatsaws.profile.repository.DynamoDbTableName
import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.Schema
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import java.net.URI
import javax.inject.Singleton

@MergeComponent(AppScope::class)
@Singleton
interface TestAppComponent : AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun localContainer(container: LocalStackContainer): Builder

    @BindsInstance
    fun queueUrl(@EventDestinationEndpoint queueUrl: String): Builder

    fun build(): AppComponent
  }
}

@Module
@ContributesTo(
  AppScope::class,
  replaces = [ConfigModule::class]
)
class TestConfigModule {


  @Singleton
  @Provides
  fun provideRegion(container: LocalStackContainer): Region {
    return Region.of(container.region)
  }

  @Singleton
  @Provides
  fun provideCredentialsProvider(container: LocalStackContainer): AwsCredentialsProvider {
    return StaticCredentialsProvider.create(
      AwsBasicCredentials.create(
        container.accessKey, container.secretKey
    ))
  }

  @DynamoDbTableName
  @Provides
  fun provideDynamoDbTableName(): String {
    return Schema.TABLE_NAME
  }

  @Singleton
  @Provides
  fun provideCloudMetrics(): CloudMetrics {
    return FakeCloudMetrics()
  }

  @DynamoDbEndpoint
  @Provides
  fun provideDynamoDbEndpoint(container: LocalStackContainer): URI {
    return container.getEndpointOverride(LocalStackContainer.Service.DYNAMODB)
  }

  @SqsEndpoint
  @Provides
  fun provideSqsEndpoint(container: LocalStackContainer): URI {
    return container.getEndpointOverride(LocalStackContainer.Service.SQS)
  }
}

