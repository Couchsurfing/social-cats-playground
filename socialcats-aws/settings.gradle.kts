import de.fayard.refreshVersions.bootstrapRefreshVersions

buildscript {
  repositories { gradlePluginPortal() }
  dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
}

bootstrapRefreshVersions()

rootProject.name = "Social Cats Aws"
include(":frontend:android")
