plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
  id("dagger.hilt.android.plugin")
}

dependencies {
  implementation(project(":feature:auth:android"))
  implementation(project(":feature:push-notifications:api"))
  implementation(project(":feature:api-common"))
  implementation(project(":library:android-datastore"))
  api(project(":feature:android-wiring"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:_")

  api("androidx.work:work-runtime-ktx:_")
  api("com.google.dagger:hilt-android:_")
  api("androidx.hilt:hilt-work:_")
  kapt("androidx.hilt:hilt-compiler:_")
  kapt("com.google.dagger:hilt-compiler:_")

  implementation("com.jakewharton.timber:timber:_")
  implementation(platform("com.google.firebase:firebase-bom:_"))
  implementation("com.google.firebase:firebase-messaging")
  implementation("com.squareup.retrofit2:retrofit:_")
  implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:_")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:_")
  api("com.squareup.okhttp3:okhttp:_")
}

kapt {
  correctErrorTypes = true
}
