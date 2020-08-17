

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
                api(project(":store:client"))
                api(project(":kotlin-util"))
                
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(project(":session:client"))
                api(Config.Libs.KotlinLogging.jdk)
                implementation(Config.Libs.Koin.android)
                implementation(Config.Libs.Firebase.messaging)
            }
        }

//        val jsMain by getting {
//            dependencies {
//                api(Config.Libs.KotlinLogging.js)
//            }
//        }
    }
}
