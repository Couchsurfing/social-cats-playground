plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                api(project(":feature-flags"))
                
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
            }
        }

        val androidMain by getting {
            dependencies {
                api(project(":themes"))

                
                api(Config.Libs.KotlinLogging.jdk)
                implementation(Config.Libs.Firebase.remoteConfig)
                implementation(Config.Libs.material)
                implementation(Config.Libs.AndroidX.constraintLayout)
                implementation(Config.Libs.processPhoenix)
            }
        }

        val jsMain by getting {
            dependencies {
                
                
                api(Config.Libs.KotlinLogging.js)
            }
        }
    }
}
