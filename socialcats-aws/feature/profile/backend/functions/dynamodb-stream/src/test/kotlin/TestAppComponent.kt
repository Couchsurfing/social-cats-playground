package com.nicolasmilliard.socialcatsaws.profile.backend.functions

import com.nicolasmilliard.di.scope.AppScope
import com.nicolasmilliard.socialcatsaws.eventpublisher.BatchEventPublisher
import com.nicolasmilliard.socialcatsaws.eventpublisher.Event
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.anvil.annotations.MergeComponent
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@MergeComponent(AppScope::class)
@Singleton
interface TestAppComponent : AppComponent

@Module
@ContributesTo(
  AppScope::class,
  replaces = [AppModule::class]
)
class TestAppModule {

  @Provides
  @Singleton
  fun provideEventPublisher(): BatchEventPublisher {
    return FakeEventBusPublisher()
  }
}

class FakeEventBusPublisher : BatchEventPublisher {
  val published: MutableList<List<Event>> = mutableListOf()

  override fun publish(events: List<Event>) {
    published.add(events)
  }
}
