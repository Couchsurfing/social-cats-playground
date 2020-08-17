plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

kotlin {
    jvm {
        val main by compilations.getting {
            java.sourceCompatibility = Config.Common.sourceCompatibility
            java.targetCompatibility = Config.Common.targetCompatibility
            kotlinOptions {
                jvmTarget = Config.Common.kotlinJvmTarget
            }
        }
    }
    js {
        browser()
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(Config.Libs.Kotlin.Serialization.core)
            }
        }
    }
}
