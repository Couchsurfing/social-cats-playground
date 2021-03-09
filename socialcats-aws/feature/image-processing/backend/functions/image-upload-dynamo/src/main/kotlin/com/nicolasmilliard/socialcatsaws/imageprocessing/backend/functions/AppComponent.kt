package com.nicolasmilliard.socialcatsaws.imageprocessing.backend.functions

import com.nicolasmilliard.cloudmetric.CloudMetricModule
import com.nicolasmilliard.cloudmetric.CloudMetrics
import com.nicolasmilliard.di.scope.AppScope
import com.nicolasmilliard.socialcatsaws.backend.repository.objectstore.S3BucketName
import com.nicolasmilliard.socialcatsaws.profile.UploadImageUseCase
import com.nicolasmilliard.socialcatsaws.profile.repository.DynamoDbTableName
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.Module
import dagger.Provides
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.core.SdkSystemSetting
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration
import software.amazon.awssdk.http.SdkHttpClient
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI
import javax.inject.Singleton

@MergeComponent(AppScope::class)
@Singleton
interface AppComponent {
  fun getCloudMetrics(): CloudMetrics
  fun getUploadImageUseCase(): UploadImageUseCase
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
  fun provideSdkHttpClient(): SdkHttpClient {
    return UrlConnectionHttpClient.builder().build()
  }

  @Singleton
  @Provides
  fun provideS3Client(region: Region, httpClient: SdkHttpClient): S3Client {
    return S3Client.builder()
      .httpClient(httpClient)
      .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
      .region(region)
      .overrideConfiguration(ClientOverrideConfiguration.builder().build())
      .endpointOverride(URI("https://s3.$region.amazonaws.com"))
      .build()
  }

  @Singleton
  @Provides
  fun provideDynamoDbClient(region: Region, httpClient: SdkHttpClient): DynamoDbClient {
    return DynamoDbClient.builder()
      .httpClient(httpClient)
      .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
      .region(region)
      .overrideConfiguration(ClientOverrideConfiguration.builder().build())
      .endpointOverride(URI("https://dynamodb.$region.amazonaws.com"))
      .build()
  }

  @S3BucketName
  @Provides
  fun provideS3BucketName(): String {
    return System.getenv("S3_BUCKET_NAME")
  }

  @DynamoDbTableName
  @Provides
  fun provideDynamoDbTableName(): String {
    return System.getenv("DDB_TABLE_NAME")
  }

  @Singleton
  @Provides
  fun provideCloudMetrics(): CloudMetrics {
    return CloudMetricModule.provideCloudMetrics(System.getenv("APP_NAME"))
  }
}
