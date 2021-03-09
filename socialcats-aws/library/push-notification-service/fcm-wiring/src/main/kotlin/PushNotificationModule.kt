package com.nicolasmilliard.socialcatsaws.backend.repository.objectstore

import com.google.firebase.messaging.FirebaseMessaging
import com.nicolasmilliard.di.scope.AppScope
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.FcmNotifications
import com.nicolasmilliard.socialcatsaws.backend.pushnotification.PushNotificationService
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@Module
@ContributesTo(AppScope::class)
public object PushNotificationModule {

  @Provides
  public fun providePushNotification(firebaseMessaging: FirebaseMessaging): PushNotificationService {
    return FcmNotifications(firebaseMessaging)
  }
}
