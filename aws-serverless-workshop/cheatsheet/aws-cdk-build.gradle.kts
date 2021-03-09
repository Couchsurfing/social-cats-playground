plugins {
  kotlin("jvm")
  `application`
}


dependencies {
  implementation(kotlin("stdlib"))

  val cdkVersion = "1.93.0"
  implementation("software.amazon.awscdk:sns-subscriptions:$cdkVersion")
  implementation("software.amazon.awscdk:sns:$cdkVersion")
  implementation("software.amazon.awscdk:sqs:$cdkVersion")

//  implementation("software.amazon.awscdk:dynamodb:$cdkVersion")
//  implementation("software.amazon.awscdk:apigateway:$cdkVersion")
//  implementation("software.amazon.awscdk:apigatewayv2:$cdkVersion")
//  implementation("software.amazon.awscdk:apigatewayv2-integrations:$cdkVersion")
//  implementation("software.amazon.awscdk:apigatewayv2-authorizers:$cdkVersion")
//  implementation("software.amazon.awscdk:lambda:$cdkVersion")
//  implementation("software.amazon.awscdk:lambda-destinations:$cdkVersion")
//  implementation("software.amazon.awscdk:logs:$cdkVersion")
//  implementation("software.amazon.awscdk:cognito:$cdkVersion")
//  implementation("software.amazon.awscdk:cdk-cloudformation-include:$cdkVersion")
//
//
//  val constructVersion = "1.90.0"
//  implementation("software.amazon.awsconstructs:core:constructVersion")
//  implementation("software.amazon.awsconstructs:lambdas3:constructVersion")
//  implementation("software.amazon.awsconstructs:lambdadynamodb:constructVersion")
//  implementation("software.amazon.awsconstructs:dynamodbstreamlambda:constructVersion")

  testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
  testImplementation("org.junit.jupiter:junit-jupiter-engine:5.7.1")
  testImplementation("org.assertj:assertj-core:3.19.0")
}

application {
  mainClass.set("com.myorg.AwsCdkAppKt")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
  kotlinOptions {
    jvmTarget = "11"
  }
}

tasks.test {
  useJUnitPlatform()
  testLogging {
    events("passed", "skipped", "failed")
  }
}

