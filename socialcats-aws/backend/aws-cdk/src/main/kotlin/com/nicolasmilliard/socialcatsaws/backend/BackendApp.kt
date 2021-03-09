package com.nicolasmilliard.socialcatsaws.backend

import com.nicolasmilliard.socialcatsaws.backend.core.Api
import com.nicolasmilliard.socialcatsaws.backend.core.Auth
import com.nicolasmilliard.socialcatsaws.backend.core.EventBusService
import com.nicolasmilliard.socialcatsaws.backend.core.ImagingService
import com.nicolasmilliard.socialcatsaws.backend.core.PushNotificationService
import com.nicolasmilliard.socialcatsaws.backend.core.S3Monitoring
import com.nicolasmilliard.socialcatsaws.backend.core.UserRepository
import com.nicolasmilliard.socialcatsaws.backend.core.conversations.ConversationsRepository
import com.nicolasmilliard.socialcatsaws.backend.util.LambdaFunction
import com.nicolasmilliard.socialcatsaws.backend.util.buildDeadLetterQueue
import com.nicolasmilliard.socialcatsaws.backend.util.buildLambdaProps
import com.nicolasmilliard.socialcatsaws.backend.util.getLambdaInsightPolicy
import software.amazon.awscdk.core.App
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Environment
import software.amazon.awscdk.core.Stack
import software.amazon.awscdk.core.StackProps
import software.amazon.awscdk.core.Tags
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions
import software.amazon.awscdk.services.apigatewayv2.HttpApi
import software.amazon.awscdk.services.apigatewayv2.HttpMethod
import software.amazon.awscdk.services.apigatewayv2.IHttpRouteAuthorizer
import software.amazon.awscdk.services.apigatewayv2.PayloadFormatVersion
import software.amazon.awscdk.services.apigatewayv2.integrations.LambdaProxyIntegration
import software.amazon.awscdk.services.cognito.UserPool
import software.amazon.awscdk.services.cognito.UserPoolOperation
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.lambda.destinations.SqsDestination
import software.amazon.awscdk.services.lambda.eventsources.S3EventSource
import software.amazon.awscdk.services.s3.Bucket
import software.amazon.awscdk.services.s3.EventType
import software.amazon.awsconstructs.services.lambdadynamodb.LambdaToDynamoDB
import java.io.File
import java.io.FileInputStream
import java.util.Properties

object BackendApp {
  @JvmStatic
  fun main(args: Array<String>) {
    val app = App()

    val functionsProp = Properties()
    FileInputStream(File(args[0])).use { functionsProp.load(it) }

    setupStack(app, "Beta", "690127176154", "us-east-1", false, functionsProp)
    // setupStack(app, "Prod", "669518591550", "us-east-1", true, functionsProp)

    app.synth()
  }
}

private fun setupStack(app: App, envName: String, accountId: String, region: String, isProd: Boolean, functionsProp: Properties) {
  val betaEnv = Environment.builder()
    .account(accountId)
    .region(region)
    .build()

  val betaStackProp = StackProps.builder()
    .env(betaEnv)
    .description("Social Cats $envName Stack")
    .terminationProtection(false)
    .build()

  SocialCatsService(
    app,
    "SocialCats${envName}Stack",
    betaStackProp,
    functionsProp,
    isProd,
    envName
  )
}

class SocialCatsService(scope: Construct, id: String, props: StackProps, functionsProp: Properties, isProd: Boolean, envName: String) :
  Stack(scope, id, props) {

  init {
    val appName = "social-cats"
    Tags.of(this).add("app", appName)
    Tags.of(this).add("stage", envName)
    val appNameWithEnv = "social-cats-$envName"

    val eventBusService = EventBusService(this, "EventBusService")
    val usersRepository = UserRepository(
      scope = this,
      id = "UsersRepository",
      isProd = isProd,
      functionsProp = functionsProp,
      region = props.env!!.region!!,
      appName = appNameWithEnv,
      eventBus = eventBusService.eventBus
    )
    ConversationsRepository(this, "ConversationsRepository", isProd)

    val auth = Auth(this, "AuthConstruct", isProd)

    val apiStack = Api(
      this,
      "ApiConstruct",
      isProd = isProd,
      throttlingBurstLimit = 10,
      throttlingRateLimit = 10,
      jwtIssuer = auth.userPool.userPoolProviderUrl,
      jwtAudiences = listOf(auth.androidClient.userPoolClientId)
    )

    val pushNotificationService = PushNotificationService(
      this,
      "PushService",
      functionsProp,
      appName = appNameWithEnv,
      isProd = isProd,
      region = props.env!!.region!!,
      table = usersRepository.dynamodbTable,
      eventBus = eventBusService.eventBus
    )

    val imageHandler = ImagingService(
      this,
      "ImagingService",
      functionsProp,
      appName = appNameWithEnv,
      isProd = isProd,
      region = props.env!!.region!!,
      table = usersRepository.dynamodbTable,
      throttlingBurstLimit = 10,
      throttlingRateLimit = 10,
    )

    glueCognitoAndDynamo(appNameWithEnv, auth.userPool, usersRepository.dynamodbTable, region, functionsProp)

    glueS3ImageBucketAndDynamo(
      appName = appNameWithEnv,
      s3ImageBucket = imageHandler.s3ImageBucket,
      dynamodbTable = usersRepository.dynamodbTable,
      functionsProp = functionsProp
    )

    val httpApi = apiStack.httpApi
    val authorizer = apiStack.authorizer

    integrateApiFunction(
      httpApi,
      authorizer,
      pushNotificationService.createDeviceFunction,
      "/v1/devices"
    )

    integrateApiFunction(
      httpApi,
      authorizer,
      imageHandler.createUploadUrlFunction,
      "/v1/images"
    )

    S3Monitoring(
      this,
      "S3Monitoring",
      isProd,
      listOf(imageHandler.s3ImageAccessLogBucket)
    )
  }

  private fun glueS3ImageBucketAndDynamo(appName: String, s3ImageBucket: Bucket, dynamodbTable: Table, functionsProp: Properties) {
    val dlq = buildDeadLetterQueue(this, "DLQ")
    val s3ImageToDynamoDB = LambdaToDynamoDB.Builder.create(this, "S3ImageLambdaToDynamo")
      .existingTableObj(dynamodbTable)
      .lambdaFunctionProps(
        buildLambdaProps(
          construct = this,
          asset = functionsProp.getProperty("image-upload-dynamo"),
          region = region,
          handler = "com.nicolasmilliard.socialcatsaws.imageprocessing.backend.functions.OnNewImage",
          description = "Function for syncing new S3 profile.image to dynamo",
          version = "1.0.19-SNAPSHOT",
          layerId = "S3ImageLambdaToDynamoLayerId",
          env = mapOf(
            "S3_BUCKET_NAME" to s3ImageBucket.bucketName,
            "APP_NAME" to appName
          ),
          onFailure = SqsDestination(dlq)
        )
      )
      .tablePermissions("ReadWrite")
      .build()
    val function = s3ImageToDynamoDB.lambdaFunction
    function.role?.addManagedPolicy(getLambdaInsightPolicy())
    function.addEventSource(
      S3EventSource.Builder.create(s3ImageBucket).events(
        listOf(EventType.OBJECT_CREATED)
      ).build()
    )
    s3ImageBucket.grantDelete(function)
  }

  private fun glueCognitoAndDynamo(appName: String, userPool: UserPool, dynamodbTable: Table, region: String, functionsProp: Properties) {
    val lambdaToDynamoDB = LambdaToDynamoDB.Builder.create(this, "CognitoLambdaToDynamo")
      .existingTableObj(dynamodbTable)
      .lambdaFunctionProps(
        buildLambdaProps(
          construct = this,
          asset = functionsProp.getProperty("cognito-confirmation-dynamo"),
          region = region,
          handler = "com.nicolasmilliard.socialcatsaws.profile.functions.OnNewAuthUser",
          description = "Function for syncing new Cognito user to dynamo",
          version = "1.0.23-SNAPSHOT",
          layerId = "CognitoLambdaToDynamoLayerId",
          env = mapOf("APP_NAME" to appName)
        )
      )
      .tablePermissions("Write")
      .build()

    lambdaToDynamoDB.lambdaFunction.role?.addManagedPolicy(getLambdaInsightPolicy())
    userPool.addTrigger(
      UserPoolOperation.POST_CONFIRMATION,
      lambdaToDynamoDB.lambdaFunction
    )
  }

  private fun integrateApiFunction(httpApi: HttpApi, authorizerRef: IHttpRouteAuthorizer, lambda: LambdaFunction, path: String) {
    val conversationsInt = LambdaProxyIntegration.Builder.create()
      .handler(lambda)
      .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
      .build()

    httpApi.addRoutes(
      AddRoutesOptions.builder()
        .methods(listOf(HttpMethod.ANY))
        .path(path)
        .authorizer(authorizerRef)
        .integration(conversationsInt)
        .build()
    )
  }
}
