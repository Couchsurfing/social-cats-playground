

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

base.archivesBaseName = "payment-presenter"

kotlin {
    android()
//    js {
//        browser()
//    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":session:client"))
                implementation(project(":connectivity"))
                api(project(":payment:client"))
                api(project(":presentation:presenter"))
                implementation(project(":kotlin-util"))
                
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":test-util"))
                implementation(project(":session:client:test-util"))
                implementation(project(":connectivity:test-util"))

                implementation(Config.Libs.Kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {

                api(Config.Libs.KotlinLogging.jdk)
            }
        }


//        val jsMain by getting {
//            dependencies {
//                
//                
//                api(Config.Libs.KotlinLogging.js)
//            }
//        }
//
//        val jsTest by getting {
//            dependencies {
//                implementation(Config.Libs.Kotlin.Test.js)
//            }
//        }
    }
}
