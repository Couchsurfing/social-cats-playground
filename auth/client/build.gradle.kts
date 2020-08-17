

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

base.archivesBaseName = "auth-client"

kotlin {
    android()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
            }
        }

        val androidMain by getting {
            dependencies {
                api(Config.Libs.KotlinLogging.jdk)
                api(Config.Libs.Firebase.auth)
                api(Config.Libs.Kotlin.Coroutine.playServices)
            }
        }

        val jsMain by getting {
            dependencies {
                api(Config.Libs.KotlinLogging.js)
            }
        }
    }
}
