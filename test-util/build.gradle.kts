plugins {
    kotlin("multiplatform")
}

base.archivesBaseName = "common-test"

kotlin {
    jvm {
        val main by compilations.getting {
            java.sourceCompatibility = Config.Android.sourceCompatibility
            java.targetCompatibility = Config.Android.targetCompatibility
            kotlinOptions {
                jvmTarget = Config.Android.kotlinJvmTarget
            }
        }
    }
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
                api(Config.Libs.turbine)
            }
        }

        val jvmMain by getting {
            dependencies {

                
                api(Config.Libs.KotlinLogging.jdk)
                api(Config.Libs.slf4jSimple)
            }
        }

        val jsMain by getting {
            dependencies {

                
                api(Config.Libs.KotlinLogging.js)
            }
        }
    }
}
