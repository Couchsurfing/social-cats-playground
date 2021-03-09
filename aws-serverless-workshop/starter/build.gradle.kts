

group = "com.nicolasmilliard.serverlessworkshop"

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.diffplug.spotless")
}

allprojects {
    repositories {
        mavenCentral()
        maven(url = "https://s3-us-west-2.amazonaws.com/dynamodb-local/release")
    }
    apply(plugin = "com.diffplug.spotless")

    spotless {
        kotlin {
            target("**/*.kt")
            ktlint("0.40.0")
        }
        kotlinGradle {
            ktlint("0.40.0")
        }
    }
}
