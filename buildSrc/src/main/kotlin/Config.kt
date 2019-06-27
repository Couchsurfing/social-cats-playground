object Config {
  const val kotlinVersion = "1.3.41"
  const val navigationVersion = "2.1.0-rc01"

  object Google {
    const val projectId = "sweat-monkey"
  }

  object Versions {
    const val name = "0.1.0"

    object AndroidApp {
      const val major = 0
      const val minor = 0
      const val patch = 1
      const val build = 0

      const val name = "$major.$minor.$patch"
      const val fullName = "$name.$build"
      const val code = major * 1000000 + minor * 10000 + patch * 100 + build
    }

    object SearchApi {
      const val version = "20190628t1837"
    }
  }

  object Plugins {
    const val appEngineGradlePlugin = "com.google.cloud.tools:appengine-gradle-plugin:2.1.0"
    const val android = "com.android.tools.build:gradle:3.5.0"
    const val googleServices = "com.google.gms:google-services:4.3.0"
    const val navigation =
      "androidx.navigation:navigation-safe-args-gradle-plugin:$navigationVersion"
    const val ossLicenses = "com.google.android.gms:oss-licenses-plugin:0.9.5"
    const val gradleVersions = "0.22.0"
    const val shadowVersion = "5.1.0"
    const val ktlint = "org.jlleitschuh.gradle:ktlint-gradle:8.2.0"
    const val kotlinGradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val kotlinFrontEnd = "org.jetbrains.kotlin:kotlin-frontend-plugin:0.0.45"
  }

  object Android {
    object SdkVersions {
      const val compile = 29
      const val target = 29
      const val min = 21
    }

    const val buildToolsVersion = "29.0.2"
  }

  object Libs {

    object WebFrontend {
      const val coroutinesCoreJs = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.2.2"
      const val htmlJs = "org.jetbrains.kotlinx:kotlinx-html-js:0.6.12"
      const val kotlinReact = "org.jetbrains:kotlin-react:16.9.0-pre.82-kotlin-1.3.41"
      const val kotlinReactDom = "org.jetbrains:kotlin-react-dom:16.9.0-pre.82-kotlin-1.3.41"
      const val kotlinReactRouterDom =
        "org.jetbrains:kotlin-react-router-dom:4.3.1-pre.82-kotlin-1.3.41"
    }

    object GoogleFunction {
      private const val servletApi = "4.0.1"

      const val gson = "com.google.code.gson:gson:2.8.5"
      const val javaServletApi = "javax.servlet:javax.servlet-api:$servletApi"
      const val functionFrameworkApi =
        "com.google.cloud.functions:functions-framework-api:1.0.0-alpha-1"

      const val junit = "junit:junit:4.12"
      const val mockito = "org.mockito:mockito-core:3.0.0"
      const val truth = "com.google.truth:truth:1.0"
    }

    object Ktor {
      private const val ktorVersion = "1.2.3"

      const val ktorServerNetty = "io.ktor:ktor-server-netty:$ktorVersion"
      const val ktorAuth = "io.ktor:ktor-auth:$ktorVersion"
      const val ktorServerTest = "io.ktor:ktor-server-tests:$ktorVersion"
      const val ktorLocation = "io.ktor:ktor-locations:$ktorVersion"

      const val ktorMoshi = "com.ryanharter.ktor:ktor-moshi:1.0.1"
    }

    object Kotlin {
      object Coroutine {
        private const val version = "1.2.1"

        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
      }
    }

    object AndroidX {
      private const val pagingVersion = "2.1.0"
      private const val fragmentVersion = "1.1.0-rc04"
      private const val workVersion = "2.2.0"

      const val appCompat = "androidx.appcompat:appcompat:1.1.0-rc01"
      const val activityKtx = "androidx.activity:activity-ktx:1.0.0-rc01"
      const val fragmentKtx = "androidx.fragment:fragment-ktx:$fragmentVersion"
      const val fragmentTesting = "androidx.fragment:fragment-testing:$fragmentVersion"
      const val preferenceKtx = "androidx.preference:preference-ktx:1.1.0-rc01"
      const val coreKtx = "androidx.core:core-ktx:1.1.0-rc03"
      const val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta2"
      const val recyclerView = "androidx.recyclerview:recyclerview:1.1.0-beta03"
      const val viewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.1.0-rc01"
      const val pagingRuntimeKtx = "androidx.paging:paging-runtime-ktx:$pagingVersion"
      const val pagingCommon = "androidx.paging:paging-common:$pagingVersion"
      const val dynamicAnimation = "androidx.dynamicanimation:dynamicanimation-ktx:1.0.0-alpha02"
      const val navigationFragmentKtx =
        "androidx.navigation:navigation-fragment-ktx:$navigationVersion"
      const val navigationUiKtx = "androidx.navigation:navigation-ui-ktx:$navigationVersion"
      const val workRuntimeKtx = "androidx.work:work-runtime-ktx:$workVersion"
      const val workTesting = "androidx.work:work-testing:$workVersion"
    }

    object PlayServices {
      const val ossLicenses = "com.google.android.gms:play-services-oss-licenses:17.0.0"
    }

    object Firebase {
      const val common = "com.google.firebase:firebase-common-ktx:19.0.0"
      const val auth = "com.google.firebase:firebase-auth:19.0.0"
      const val firestore = "com.google.firebase:firebase-firestore-ktx:21.0.0"
      const val uiAuth = "com.firebaseui:firebase-ui-auth:5.0.0"
      const val uiFirestore = "com.firebaseui:firebase-ui-firestore:5.0.0"
    }

    object Dagger {
      private const val version = "2.24"
      private const val assistedInjectVersion = "0.5.0"

      const val core = "com.google.dagger:dagger:$version"
      const val compiler = "com.google.dagger:dagger-compiler:$version"
      const val android = "com.google.dagger:dagger-android-support:$version"
      const val androidProcessor = "com.google.dagger:dagger-android-processor:$version"

      const val assistedInject =
        "com.squareup.inject:assisted-inject-annotations-dagger2:$assistedInjectVersion"
      const val assistedInjectProcessor =
        "com.squareup.inject:assisted-inject-processor-dagger2:$assistedInjectVersion"
    }

    object LeakCanary {
      private const val version = "1.6.3"

      const val leakCanary = "com.squareup.leakcanary:leakcanary-android:$version"
      const val leakCanaryFragments =
        "com.squareup.leakcanary:leakcanary-support-fragment:$version"
      const val leakCanaryNoop =
        "com.squareup.leakcanary:leakcanary-android-no-op:$version"
    }

    object Moshi {
      private const val version = "1.8.0"

      const val moshiKotlinCodeGen = "com.squareup.moshi:moshi-kotlin-codegen:$version"
      const val moshiAdapter = "com.squareup.moshi:moshi-adapters:$version"
    }

    object Test {
      const val androidxJunit = "androidx.test.ext:junit:1.1.1"
      const val androidxtruth = "androidx.test.ext:truth:1.2.0"
      const val robolectric = "org.robolectric:robolectric:4.3"

      const val espressoCore = "androidx.test.espresso:espresso-core:3.2.0"
    }

    // Misc
    const val material = "com.google.android.material:material:1.1.0-alpha09"
    const val timber = "com.jakewharton.timber:timber:4.7.1"
    const val picasso = "com.squareup.picasso:picasso:2.71828"
    const val okHttp = "com.squareup.okhttp3:okhttp:4.1.0"
    const val okIo = "com.squareup.okio:okio:2.3.0"
    const val shimmer = "com.facebook.shimmer:shimmer:0.5.0"

    private const val kotlinLoggingVersion = "1.7.6"
    const val kotlinLogging = "io.github.microutils:kotlin-logging:$kotlinLoggingVersion"
    const val kotlinLoggingJs = "io.github.microutils:kotlin-logging-js:$kotlinLoggingVersion"
    const val logBackClassic = "ch.qos.logback:logback-classic:1.2.3"
    const val log4jToSlf4j = "org.apache.logging.log4j:log4j-to-slf4j:2.12.1"
    const val firebaseAdmin = "com.google.firebase:firebase-admin:6.9.0"
    const val awsJavaSdk = "software.amazon.awssdk:bom:2.7.27"
    const val awsJavaSdkApacheClient = "software.amazon.awssdk:apache-client"
    const val awsJavaSdkApacheAuth = "software.amazon.awssdk:auth"
    const val elasticSearchHighLevelClient =
      "org.elasticsearch.client:elasticsearch-rest-high-level-client:6.7.2"
  }
}
