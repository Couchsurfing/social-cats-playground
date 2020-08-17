

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
    id("com.google.firebase.appdistribution")
}

android {

    val isCiBuild = rootProject.extra["isCiBuild"] as Boolean
    val gCloudServiceKey = rootProject.extra["gCloudServiceKey"] as String?

    defaultConfig {
        applicationId = "com.nicolasmilliard.socialcats"

        versionCode = Config.Android.Versions.code
        versionName = Config.Android.Versions.name

        resConfigs("en")

        buildConfigField("boolean", "IS_CI_BUILD", "false")
        buildConfigField("String", "COMMIT_SHA", "\"${gitSha()}\"")
        buildConfigField("long", "COMMIT_UNIX_TIMESTAMP", "${gitTimestamp()}L")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            applicationIdSuffix = ".debug"
            signingConfig = signingConfigs.getByName("debug")
            buildConfigField("boolean", "IS_CI_BUILD", isCiBuild.toString())
        }

        getByName("release") {
            signingConfig = if (file("upload.jks").exists()) {
                signingConfigs.getByName("upload")
            } else {
                signingConfigs.getByName("debug")
            }
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles("src/main/shrinker-rules.pro")
            firebaseAppDistribution {
                groups = "internal"
                if (!gCloudServiceKey.isNullOrBlank()) {
                    serviceCredentialsFile = getGCloudKeyFilePath(gCloudServiceKey)
                }
            }
        }
    }

    packagingOptions {
        exclude("META-INF/*.kotlin_module")
    }
}

dependencies {

    implementation(project(":frontend:android:base"))
    implementation(project(":search:ui-android"))
    implementation(project(":payment:ui-android"))
    implementation(project(":account:ui-android"))
    implementation(Config.Libs.LeakCanary.plumber)

    debugImplementation(project(":test-settings"))
    debugImplementation(project(":cloud-messaging:client"))
    debugImplementation(Config.Libs.Play.core)
    debugImplementation(Config.Libs.LeakCanary.android)
    debugImplementation(Config.Libs.Koin.core)

    testImplementation(Config.Libs.Test.androidxJunit)
    testImplementation(Config.Libs.Test.androidxtruth)
    testImplementation(Config.Libs.Test.espressoCore)
    testImplementation(Config.Libs.Test.robolectric)
//    testImplementation(Config.Libs.Kotlin.Coroutine.test)
    testImplementation(Config.Libs.AndroidX.pagingCommon)
    testImplementation(Config.Libs.AndroidX.fragmentTesting)
    testImplementation(Config.Libs.AndroidX.workTesting)
}

// Because the components are created only during the afterEvaluate phase, you must
// configure your publications using the afterEvaluate() lifecycle method.
afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("release") {
                // Applies the component for the release build variant.
                from(components.findByName("release_aab"))

                artifactId = "$rootProject.name-$project.name"
                version = Config.Android.Versions.name
            }
        }
    }
}

apply(plugin = "com.google.gms.google-services")
