plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
  id("dagger.hilt.android.plugin")
}

apply(from = "android.gradle")
apply(plugin = "app.cash.exhaustive")

dependencies {

  implementation(Kotlin.stdlib)
  implementation(KotlinX.coroutines)

  implementation(AndroidX.core.ktx)
  implementation(AndroidX.lifecycle.runtimeKtx)

  implementation(AndroidX.compose.ui)
  implementation(AndroidX.compose.material)
  implementation("androidx.compose.material:material-icons-extended:_")
  implementation("androidx.compose.ui:ui-tooling:_")
  implementation("androidx.navigation:navigation-compose:1.0.0-alpha04")
  implementation("androidx.navigation:navigation-runtime-ktx:_")

  implementation("com.google.dagger:hilt-android:_")
  kapt("com.google.dagger:hilt-android-compiler:_")
  implementation("androidx.hilt:hilt-lifecycle-viewmodel:_")
  kapt("androidx.hilt:hilt-compiler:_")

  testImplementation("junit:junit:_")

  androidTestImplementation(AndroidX.test.ext.junitKtx)
  androidTestImplementation(AndroidX.test.espresso.core)
}
