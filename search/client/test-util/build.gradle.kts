plugins {
    kotlin("multiplatform")
}

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
                api(project(":api:social-cats"))
                api(project(":search:client"))
                
                api(Config.Libs.Kotlin.Coroutine.core)
            }
        }
        val jvmMain by getting {
            dependencies {

                
            }
        }

        val jsMain by getting {
            dependencies {
                
                
            }
        }
    }
}
