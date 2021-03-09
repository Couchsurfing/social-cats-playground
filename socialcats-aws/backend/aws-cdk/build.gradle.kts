plugins {
  kotlin("jvm")
  `application`
}

val functionJars by configurations.creating {
}

dependencies {
  implementation(project(":feature:profile:backend:repository:dynamodb-impl:schema"))
  implementation(project(":feature:event-registry"))
  implementation(kotlin("stdlib"))

  implementation("software.amazon.awscdk:dynamodb:_")
  implementation("software.amazon.awscdk:apigateway:_")
  implementation("software.amazon.awscdk:apigatewayv2:_")
  implementation("software.amazon.awscdk:apigatewayv2-integrations:_")
  implementation("software.amazon.awscdk:apigatewayv2-authorizers:_")
  implementation("software.amazon.awscdk:lambda:_")
  implementation("software.amazon.awscdk:lambda-destinations:_")
  implementation("software.amazon.awscdk:core:_")
  implementation("software.amazon.awscdk:logs:_")
  implementation("software.amazon.awscdk:cognito:_")
  implementation("software.amazon.awscdk:cdk-cloudformation-include:_")
  implementation("software.amazon.awscdk:ec2:_")
  implementation("software.amazon.awscdk:glue:_")
  implementation("software.amazon.awscdk:waf:_")
  implementation("software.amazon.awscdk:sqs:_")
  implementation("software.amazon.awscdk:events:_")
  implementation("software.amazon.awscdk:events-targets:_")
  implementation("software.amazon.awscdk:secretsmanager:_")

  implementation("software.amazon.awsconstructs:core:_")
  implementation("software.amazon.awsconstructs:lambdas3:_")
  implementation("software.amazon.awsconstructs:lambdadynamodb:_")
  implementation("software.amazon.awsconstructs:dynamodbstreamlambda:_")
  implementation("software.amazon.awsconstructs:sqslambda:_")
  implementation("software.amazon.awsconstructs:lambdasqs:_")

  testImplementation("org.junit.jupiter:junit-jupiter-api:_")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:_")
  testImplementation("org.assertj:assertj-core:_")

  functionJars(project(":feature:auth:backend:functions:cognito-confirmation-dynamo", configuration = "shadow"))
  functionJars(project(":feature:image-processing:backend:functions:image-upload-url", configuration = "shadow"))
  functionJars(project(":feature:image-processing:backend:functions:image-upload-dynamo", configuration = "shadow"))
  functionJars(project(":feature:push-notifications:backend:functions:create-device-dynamo", configuration = "shadow"))
  functionJars(project(":feature:push-notifications:backend:functions:process-image-notification", configuration = "shadow"))
  functionJars(project(":feature:push-notifications:backend:functions:send-notification", configuration = "shadow"))
  functionJars(project(":feature:profile:backend:functions:dynamodb-stream", configuration = "shadow"))
}

tasks.register<Copy>("copyFunctionJars") {
  from(functionJars)
  into("$buildDir/functions")
}

tasks.register("generateFunctionPath") {
  doLast {
    val functions: File = tasks["copyFunctionJars"].outputs.files.singleFile
    val f = File("$buildDir/function_paths.properties")
      .printWriter().use { out ->
        functions.walk().forEach {
          if (!it.isDirectory) {
            val noSnap = it.name.removeSuffix(".jar").removeSuffix("-all").removeSuffix("-SNAPSHOT")
            val indexOfLast = noSnap.lastIndexOf("-")
            val name = noSnap.take(indexOfLast)
            out.println("$name=$it")
          }
        }
      }
  }
}

tasks["generateFunctionPath"].dependsOn(tasks["copyFunctionJars"])
tasks["run"].dependsOn(tasks["generateFunctionPath"])

application {
  mainClassName = "com.nicolasmilliard.socialcatsaws.backend.BackendApp"
}

tasks.withType<org.gradle.api.tasks.JavaExec>().configureEach {
  args = listOf("$buildDir/function_paths.properties")
}

group = "com.nicolasmilliard.socialcats.backend"
version = "0.1"
description = "backend"
