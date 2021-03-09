plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
  id("dagger.hilt.android.plugin")
}

android {
  composeOptions {
    kotlinCompilerExtensionVersion = "1.0.0-beta01"
  }
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")
  implementation("com.jakewharton.timber:timber:_")
  implementation("com.amplifyframework:aws-auth-cognito:_")
  api("com.google.dagger:hilt-android:_")
  kapt("com.google.dagger:hilt-android-compiler:_")
  implementation("androidx.startup:startup-runtime:_")
}

kapt {
  correctErrorTypes = true
}
