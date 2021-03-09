package com.nicolasmilliard.socialcatsaws.profile.repository

import com.nicolasmilliard.cloudmetric.CloudMetrics
import com.nicolasmilliard.di.scope.AppScope
import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.UsersDynamoDb
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import javax.inject.Qualifier

@Module
@ContributesTo(AppScope::class)
public object UsersRepositoryModule {

  @Provides
  public fun provideUsersRepository(
    @DynamoDbTableName tableName: String,
    dynamoClient: DynamoDbClient,
    cloudMetrics: CloudMetrics
  ): UsersRepository {
    return UsersDynamoDb(dynamoClient, tableName, cloudMetrics)
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class DynamoDbTableName
