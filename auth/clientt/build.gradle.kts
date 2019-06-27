import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

kotlin {
    android()
    js().compilations["main"].kotlinOptions {
        moduleKind = "umd"
    }
    sourceSets {
        commonMain {
            dependencies {
                api(project(":api:social-cats"))
                api(Config.Libs.Kotlin.common)
                api(Config.Libs.Kotlin.Coroutine.common)
                implementation(Config.Libs.KotlinLogging.common)
            }
        }

        val androidMain by getting {
            dependencies {
                api(Config.Libs.Kotlin.jdk8)
                api(Config.Libs.Kotlin.Coroutine.jdk8)
                api(Config.Libs.KotlinLogging.jdk)
                api(Config.Libs.Firebase.auth)
                api(Config.Libs.Kotlin.Coroutine.playServices)
            }
        }

        val jsMain by getting {
            dependencies {
                api(Config.Libs.Kotlin.js)
                api(Config.Libs.Kotlin.Coroutine.js)
                api(Config.Libs.KotlinLogging.js)
            }
        }
    }
}

android {
    compileSdkVersion(Config.Android.SdkVersions.compile)

    buildToolsVersion = Config.Android.buildToolsVersion

    defaultConfig {
        minSdkVersion(Config.Android.SdkVersions.min)
    }

    lintOptions {
        textReport = true
        textOutput("stdout")
        setLintConfig(rootProject.file("lint.xml"))
        isCheckReleaseBuilds = false
    }
}
