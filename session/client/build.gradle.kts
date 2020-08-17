

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

base.archivesBaseName = "session"

kotlin {
    android()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                api(project(":auth:client"))
                api(project(":store:client"))
                api(project(":kotlin-util"))
                
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":test-util"))
                implementation(project(":session:client:test-util"))
                implementation(project(":store:client:test-util"))
                implementation(project(":auth:client:test-util"))

                implementation(Config.Libs.Kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                api(Config.Libs.KotlinLogging.jdk)
                api(Config.Libs.Firebase.messaging)
            }
        }

        val jsMain by getting {
            dependencies {
                api(Config.Libs.KotlinLogging.js)
            }
        }
    }
}
