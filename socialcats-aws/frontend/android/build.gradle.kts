plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.kapt")
  id("dagger.hilt.android.plugin")
  id("com.google.gms.google-services")
}

android {
  defaultConfig {
    applicationId = findStringProperty("applicationId")
  }

  signingConfigs {
    getByName("debug") {
      storeFile = file("debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
    create("upload") {
      storeFile = file("upload.jks")
      storePassword = System.getenv("socialcats.upload.password")
      keyAlias = "upload"
      keyPassword = System.getenv("socialcats.upload.key.password")
    }
  }

  buildTypes {
    getByName("debug") {
      minifyEnabled(false)
      applicationIdSuffix = ".debug"
      signingConfig = signingConfigs.getByName("debug")
    }

    getByName("release") {
      signingConfig = if (file("upload.jks").exists()) {
        signingConfigs.getByName("upload")
      } else {
        signingConfigs.getByName("debug")
      }
      minifyEnabled(true)
      proguardFiles("shrinker-rules.pro")
    }
  }
  compileOptions {
    isCoreLibraryDesugaringEnabled = true
  }

  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = Config.Android.composeVersion
  }
}

hilt {
  enableExperimentalClasspathAggregation = true
}

dependencies {
  implementation(project(":themes:android-ui"))
  implementation(project(":feature:api-common"))
  implementation(project(":feature:push-notifications:android-client"))
  implementation(project(":feature:home:android-ui"))
  implementation(project(":feature:profile:android-ui"))

  coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:_")

  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:_")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:_")

  implementation("com.jakewharton.timber:timber:_")
  implementation(AndroidX.core.ktx)
  implementation(AndroidX.lifecycle.runtimeKtx)

  implementation(AndroidX.compose.ui)
  implementation(AndroidX.compose.material)
  implementation("androidx.compose.material:material-icons-extended:_")
  implementation("androidx.compose.ui:ui-tooling:_")
  implementation("androidx.navigation:navigation-compose:_")
  implementation("androidx.navigation:navigation-runtime-ktx:_")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha02")
  implementation("androidx.activity:activity-compose:_")

  implementation("com.google.dagger:hilt-android:_")
  kapt("com.google.dagger:hilt-android-compiler:_")
  implementation("androidx.hilt:hilt-lifecycle-viewmodel:_")
  kapt("androidx.hilt:hilt-compiler:_")
  implementation("androidx.hilt:hilt-navigation:_")

  implementation("com.amplifyframework:core:_")
  implementation("androidx.work:work-runtime-ktx:_")

  implementation("com.squareup.okhttp3:logging-interceptor:_")

  implementation("androidx.startup:startup-runtime:_")

  testImplementation("junit:junit:_")

  androidTestImplementation(AndroidX.test.ext.junitKtx)
  androidTestImplementation(AndroidX.test.espresso.core)
}

kapt {
  correctErrorTypes = true
}
