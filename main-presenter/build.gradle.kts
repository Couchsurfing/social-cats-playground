

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

base.archivesBaseName = "search-presenter"

kotlin {
    android()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                api(project(":session:client"))
                api(project(":auth:ui"))
                api(project(":presentation:presenter"))
                
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":test-util"))
                implementation(project(":session:client:test-util"))
                implementation(Config.Libs.Kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                api(Config.Libs.KotlinLogging.jdk)
            }
        }

        val jsMain by getting {
            dependencies {

                api(Config.Libs.KotlinLogging.js)
            }
        }

    }
}
