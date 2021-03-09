import org.gradle.api.JavaVersion

object Config {
    object Java {
        val sourceCompatibility = JavaVersion.VERSION_11
        val targetCompatibility = JavaVersion.VERSION_11
    }
    object Kotlin {
        const val jvmTarget = "11"
    }
    object Android {
        object SdkVersions {
            const val compile = 30
            const val target = 30
            const val min = 26
        }
        const val buildToolsVersion = "30.0.3"

        val sourceCompatibility = JavaVersion.VERSION_11
        val targetCompatibility = JavaVersion.VERSION_11

        object Versions {
            const val major = 0
            const val minor = 0
            const val patch = 1
            const val build = 0

            const val name = "$major.$minor.$patch"
            const val fullName = "$name.$build"
            const val code = major * 1000000 + minor * 10000 + patch * 100 + build
        }

        const val composeVersion = "1.0.0-beta03"
    }
}
