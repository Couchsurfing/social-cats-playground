plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
  id("dagger.hilt.android.plugin")
}

android {
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = Config.Android.composeVersion
  }
}

dependencies {
  api(project(":feature:auth:android"))
  api(project(":feature:image-processing:android"))
  api(project(":library:android-activity-result"))
  api(project(":library:android-text-resource"))

  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")

  implementation(AndroidX.lifecycle.runtimeKtx)
  implementation(AndroidX.compose.ui)
  implementation(AndroidX.compose.material)
  implementation("androidx.compose.material:material-icons-extended:_")
  implementation("androidx.compose.ui:ui-tooling:_")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha02")
  implementation("dev.chrisbanes.accompanist:accompanist-coil:_")
  api("io.coil-kt:coil:_")

  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")
  implementation("com.jakewharton.timber:timber:_")
  api("com.google.dagger:hilt-android:_")
  kapt("com.google.dagger:hilt-android-compiler:_")
}

kapt {
  correctErrorTypes = true
}
