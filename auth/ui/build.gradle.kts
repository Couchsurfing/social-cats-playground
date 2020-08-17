

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

kotlin {
    android()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                api(project(":auth:client"))
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
            }
        }

        val androidMain by getting {
            dependencies {
                api(Config.Libs.KotlinLogging.jdk)
                api(Config.Libs.Firebase.uiAuth)
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
