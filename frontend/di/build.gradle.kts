

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

base.archivesBaseName = "di"

kotlin {
    android()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                api(project(":analytics"))
                implementation(project(":auth:client"))
                implementation(project(":store:client"))
                implementation(project(":session:client"))
                implementation(project(":bug-reporter"))
                implementation(project(":connectivity"))
                implementation(project(":cloud-messaging:client"))
                implementation(project(":feature-flags"))
                
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
                api(Config.Libs.Koin.core)
            }
        }

        commonTest {
            dependencies {
                implementation(project(":test-util"))
                implementation(Config.Libs.Kotlin.test)
                implementation(Config.Libs.Koin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(project(":cloud-messaging:android"))


                api(Config.Libs.KotlinLogging.jdk)

                implementation(Config.Libs.OkHttp.client)
                implementation(Config.Libs.OkHttp.logging)
                implementation(Config.Libs.byteUnits)
                implementation(Config.Libs.coil)
            }
        }


        val jsMain by getting {
            dependencies {
                api(Config.Libs.KotlinLogging.js)
            }
        }
    }
}
