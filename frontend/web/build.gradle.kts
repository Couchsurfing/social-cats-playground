plugins {
    id("kotlin2js")
    id("kotlin-dce-js")
    id("org.jetbrains.kotlin.frontend")
    id("distribution")
    id("com.github.ben-manes.versions")
}

group = "com.nicolasmilliard.testjs"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(Config.Libs.Kotlin.js)
    implementation(Config.Libs.Kotlin.Coroutine.js)
    implementation(Config.Libs.WebFrontend.htmlJs)
    implementation(Config.Libs.WebFrontend.kotlinReact)
    implementation(Config.Libs.WebFrontend.kotlinReactDom)
    implementation(Config.Libs.WebFrontend.kotlinReactRouterDom)
    implementation(Config.Libs.KotlinLogging.js)
}

tasks {
    kotlinFrontend {
        webpack {
            bundleName = "main"
            contentPath = file("src/main/web")
            mode = "production"
        }
        npm {
            dependency("firebase", "6.4.0")
            dependency("react-firebaseui", "4.0.0")
            dependency("react", "16.8.6")
            dependency("react-dom", "16.8.6")
            dependency("react-router-dom", "5.0.1")
            devDependency("style-loader", "1.0.0")
            devDependency("css-loader", "3.2.0")
        }
    }
    compileKotlin2Js {
        kotlinOptions {
            moduleKind = "commonjs"
            sourceMap = true
        }
    }

    distributions {
        main {
            baseName = "main"

            contents {
                from("src/main/web")
                from("$buildDir/bundle/main.bundle.js")
            }
        }
    }
    distTar {
        enabled = false
    }

    val unzip = register("unzipDist", Copy::class) {
        from(zipTree("$buildDir/distributions/main-1.0-SNAPSHOT.zip"))
        into("$buildDir/distributions")
    }

    distZip {
        finalizedBy(unzip)
    }
}

/**
 * Configures the [webpackBundle][org.jetbrains.kotlin.gradle.frontend.webpack.WebPackExtension] kotlin-frontend plugin extension.
 */
fun org.jetbrains.kotlin.gradle.frontend.KotlinFrontendExtension.`webpack`(
    configure: org.jetbrains.kotlin.gradle.frontend.webpack.WebPackExtension.() -> Unit
) {
    bundle("webpack", delegateClosureOf(configure))
}
