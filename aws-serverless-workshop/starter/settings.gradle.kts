rootProject.name = "ServerlessWorkshop"

pluginManagement {
    plugins {
        val kotlinVersion = "1.4.31"
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.serialization") version kotlinVersion
        id("com.diffplug.spotless") version "5.11.0"
        id("com.github.johnrengelman.shadow") version "6.1.0"
    }
}
include("messaging-store")
include("messaging-store:schema")
include("messaging-store:models")

