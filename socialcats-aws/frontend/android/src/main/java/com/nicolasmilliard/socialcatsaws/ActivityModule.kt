package com.nicolasmilliard.socialcatsaws

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object ActivityModule {

  @ActivityRetainedScoped
  @Provides
  fun provideActivityResultFlow() = ActivityResultFlow()
}
