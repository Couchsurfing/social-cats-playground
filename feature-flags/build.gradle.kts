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
                implementation(project(":kotlin-util"))
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
                implementation(Config.Libs.statelyIsolate)
                implementation(Config.Libs.statelyIsoCollections)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":test-util"))
                implementation(Config.Libs.Kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                api(Config.Libs.KotlinLogging.jdk)
                implementation(Config.Libs.Firebase.remoteConfig)
                implementation(Config.Libs.timber)
                implementation(Config.Libs.Kotlin.Coroutine.playServices)
            }
        }

        val jsMain by getting {
            dependencies {
                
                api(Config.Libs.KotlinLogging.js)
            }
        }
    }
}
