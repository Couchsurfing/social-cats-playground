plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
  id("dagger.hilt.android.plugin")
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
  api("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")

  implementation("androidx.datastore:datastore-preferences:_")
  api("com.google.dagger:hilt-android:_")
  kapt("com.google.dagger:hilt-compiler:_")
}

kapt {
  correctErrorTypes = true
}
