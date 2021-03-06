object Plugins {
   object Ids {
       const val DETEKT = "io.gitlab.arturbosch.detekt"
       const val KTLINT_GRADLE = "org.jlleitschuh.gradle.ktlint"
       const val ANDROID_APPLICATION = "com.android.application"
       const val ANDROID_DYNAMIC_FEATURE = "com.android.dynamic-feature"
       const val ANDROID_LIBRARY = "com.android.library"
       const val KOTLIN_MULTIPLATFORM = "org.jetbrains.kotlin.multiplatform"
       const val KOTLIN_JVM = "org.jetbrains.kotlin.jvm"
       const val KOTLIN_ANDROID = "org.jetbrains.kotlin.android"
       const val KOTLIN_ANDROID_EXTENSIONS = "org.jetbrains.kotlin.android.extensions"
       const val GRADLE_VERSION_PLUGIN = "com.github.ben-manes.versions"
       const val SAFE_ARGS = "androidx.navigation.safeargs.kotlin"
       const val ANDROID_OSS_LICENSES = "com.google.android.gms.oss-licenses-plugin"
       const val MAVEN_PUBLISH = "org.gradle.maven-publish"
   }
    object Versions  {
        const val ktlint = "0.39.0"
    }
    object OldWay  {
        const val android = "com.android.tools.build:gradle:4.2.0-beta01"
        const val appDistribution = "com.google.firebase:firebase-appdistribution-gradle:2.0.1"
        const val googleServices = "com.google.gms:google-services:4.3.4"
        const val crashlytics =  "com.google.firebase:firebase-crashlytics-gradle:2.3.0"
        const val firebasePerformance = "com.google.firebase:perf-plugin:1.3.3"
        const val navigation =
            "androidx.navigation:navigation-safe-args-gradle-plugin:${Config.Android.navigationVersion}"
        const val atomicFu = "org.jetbrains.kotlinx:atomicfu-gradle-plugin:0.14.4"
    }
}

val ANDROID_PLUGIN_IDS = setOf(
    Plugins.Ids.ANDROID_APPLICATION,
    Plugins.Ids.ANDROID_LIBRARY,
    Plugins.Ids.ANDROID_DYNAMIC_FEATURE
)
