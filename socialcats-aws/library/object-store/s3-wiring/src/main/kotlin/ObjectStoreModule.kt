package com.nicolasmilliard.socialcatsaws.backend.repository.objectstore

import com.nicolasmilliard.di.scope.AppScope
import com.nicolasmilliard.socialcatsaws.backend.repository.objectstore.s3.S3ObjectStore
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import software.amazon.awssdk.services.s3.S3Client
import javax.inject.Qualifier

@Module
@ContributesTo(AppScope::class)
public object ObjectStoreModule {

  @Provides
  public fun provideObjectStore(s3Client: S3Client, @S3BucketName bucketName: String): ObjectStore {
    return S3ObjectStore(s3Client, bucketName)
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
public annotation class S3BucketName
