plugins {
    kotlin("jvm")
}

dependencies {
    api(project(":messaging-store:models"))
    implementation(project(":messaging-store:schema"))
    implementation(kotlin("stdlib"))

    api(platform("software.amazon.awssdk:bom:2.16.16"))
    api("software.amazon.awssdk:dynamodb-enhanced")

    testImplementation(platform("org.junit:junit-bom:5.7.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.amazonaws:DynamoDBLocal:1.15.0")
    testImplementation("com.almworks.sqlite4java:sqlite4java:1.0.392")
    testImplementation("software.amazon.awssdk:url-connection-client")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
    }
}

configurations["testImplementation"].isCanBeResolved = true
tasks.register<Copy>("copyNativeTestDeps") {
    from(configurations["testImplementation"]) {
        include("*.dll")
        include("*.dylib")
        include("*.so")
    }
    into("$buildDir/libs")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
    dependsOn(tasks["copyNativeTestDeps"])
}
