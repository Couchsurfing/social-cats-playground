package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.amazonaws.services.lambda.runtime.events.SQSEvent
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.nicolasmilliard.cloudmetric.CloudMetrics
import com.nicolasmilliard.cloudmetric.FakeCloudMetrics
import com.nicolasmilliard.di.scope.AppScope
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.PushNotificationService
import com.nicolasmilliard.socialcatsaws.backend.repository.objectstore.PushNotificationModule
import com.nicolasmilliard.socialcatsaws.eventsource.BatchEventSource
import com.nicolasmilliard.socialcatsaws.profile.repository.UsersRepository
import com.nicolasmilliard.socialcatsaws.profile.repository.UsersRepositoryModule
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@MergeComponent(AppScope::class)
@Singleton
interface TestAppComponent : AppComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun fakeUserRepository(repository: FakeUsersRepository): Builder
    @BindsInstance
    fun fakePushNotification(pushNotification: FakePushNotification): Builder
    @BindsInstance
    fun fakeSqsEventSource(eventSource: FakeSqsEventSource): Builder

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
  fun provideCloudMetrics(): CloudMetrics {
    return FakeCloudMetrics()
  }
}

@Module
@ContributesTo(
  AppScope::class,
  replaces = [AppModule::class]
)
class TestAppModule {


  @Singleton
  @Provides
  fun provideObjectMapper(): ObjectMapper {
    return jacksonObjectMapper()
  }

  @Singleton
  @Provides
  fun provideBatchEventSource(fakeSqsEventSource: FakeSqsEventSource): BatchEventSource<SQSEvent.SQSMessage> {
    return fakeSqsEventSource
  }
}

@Module
@ContributesTo(AppScope::class,
  replaces = [UsersRepositoryModule::class])
public object TestUsersRepositoryModule {

  @Provides
  public fun provideUsersRepository(fakeUsersRepository: FakeUsersRepository): UsersRepository {
    return fakeUsersRepository
  }
}

@Module
@ContributesTo(AppScope::class,
replaces = [PushNotificationModule::class])
public object TestPushNotificationModule {

  @Provides
  public fun providePushNotification(fakePushNotification: FakePushNotification): PushNotificationService {
    return fakePushNotification
  }
}


