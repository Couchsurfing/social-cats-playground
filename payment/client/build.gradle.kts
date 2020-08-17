plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
//    js {
//        browser()
//    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":api:social-cats"))
                api(project(":payment:model"))
                
                api(Config.Libs.Kotlin.Coroutine.core)
                implementation(Config.Libs.KotlinLogging.common)
                api(project(":kotlin-util"))
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
                implementation(Config.Libs.Play.billing)
                api(Config.Libs.KotlinLogging.jdk)
                api(Config.Libs.Stripe.android)
            }
        }

//        js().compilations["main"].defaultSourceSet {
//            dependencies {
//
//                api(Config.Libs.KotlinLogging.js)
//            }
//        }
    }
}
