plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
  id("dagger.hilt.android.plugin")
  kotlin("plugin.serialization")
}

dependencies {
  implementation(project(":feature:auth:android"))
  implementation(project(":feature:api-common"))
  implementation(project(":feature:image-processing:api"))
  implementation(project(":feature:image-processing:api:model"))
  api(project(":library:sharp"))
  api("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")
  implementation("com.jakewharton.timber:timber:_")
  implementation("com.github.dhaval2404:imagepicker:_")

  implementation("com.squareup.retrofit2:retrofit:_")
  implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:_")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:_")
  api("com.squareup.okhttp3:okhttp:_")
  api("androidx.work:work-runtime-ktx:_")
  api("com.google.dagger:hilt-android:_")
  api("androidx.hilt:hilt-work:_")
  kapt("androidx.hilt:hilt-compiler:_")
  kapt("com.google.dagger:hilt-compiler:_")
  implementation("io.coil-kt:coil:_")
}

kapt {
  correctErrorTypes = true
}
