plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

base.archivesBaseName = "store"

kotlin {
    android()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                api(project(":store:common"))
                
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
            }
        }

        val androidMain by getting {
            dependencies {

                
                api(Config.Libs.KotlinLogging.jdk)
                implementation(Config.Libs.Kotlin.Coroutine.playServices)
                api(Config.Libs.Firebase.firestore)
                implementation(Config.Libs.guavaAndroid) {
                    because("Firestore import a version of guava with ListenableFuture class causing duplicate with firebase work listenable dependency") // ktlint-disable
                }
                api(Config.Libs.AndroidX.workRuntimeKtx)
                api(Config.Libs.timber)
            }
        }

        val jsMain by getting {
            dependencies {

                
                api(Config.Libs.KotlinLogging.js)
            }
        }
    }
}
