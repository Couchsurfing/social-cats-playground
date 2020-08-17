

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

base.archivesBaseName = "analytics"

kotlin {
    android()
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(Config.Libs.KotlinLogging.common)
            }
        }

        val androidMain by getting {
            dependencies {
                api(Config.Libs.KotlinLogging.jdk)
                implementation(Config.Libs.Firebase.analytics)
                api(Config.Libs.AndroidX.coreKtx)
            }
        }

        val jsMain by getting {
            dependencies {
                api(Config.Libs.KotlinLogging.js)
            }
        }
    }
}
