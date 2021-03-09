package com.nicolasmilliard.socialcatsaws.backend.core

import com.nicolasmilliard.socialcatsaws.backend.util.LambdaFunction
import com.nicolasmilliard.socialcatsaws.backend.util.buildDeadLetterQueue
import com.nicolasmilliard.socialcatsaws.backend.util.buildLambdaProps
import com.nicolasmilliard.socialcatsaws.backend.util.getLambdaInsightPolicy
import com.nicolasmilliard.socialcatsaws.eventregistry.EventRegistry
import org.jetbrains.annotations.NotNull
import software.amazon.awscdk.core.CfnOutput
import software.amazon.awscdk.core.CfnOutputProps
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Duration
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.events.EventBus
import software.amazon.awscdk.services.events.EventPattern
import software.amazon.awscdk.services.events.Rule
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.secretsmanager.Secret
import software.amazon.awscdk.services.secretsmanager.SecretAttributes
import software.amazon.awscdk.services.sqs.Queue
import software.amazon.awscdk.services.sqs.QueueProps
import software.amazon.awsconstructs.services.lambdadynamodb.LambdaToDynamoDB
import software.amazon.awsconstructs.services.lambdasqs.LambdaToSqs
import software.amazon.awsconstructs.services.sqslambda.SqsToLambda
import java.util.Properties

class PushNotificationService(
  scope: Construct,
  id: String,
  private val functionsProp: Properties,
  isProd: Boolean,
  private val appName: String,
  private val region: String,
  private val table: Table,
  eventBus: EventBus,
) :
  Construct(scope, id) {

  val createDeviceFunction: LambdaFunction

  init {
    createDeviceFunction = createDeviceFunction()

    val sendNotificationLambda = createSendNotificationLambda()
    table.grantWriteData(sendNotificationLambda.lambdaFunction)

    val processNotification = createProcessNotificationLambda(sendNotificationLambda.sqsQueue)

    sendNotificationLambda.lambdaFunction.addEnvironment(
      "SOURCE_QUEUE_URL",
      sendNotificationLambda.sqsQueue.queueUrl
    )

    processNotification.lambdaFunction.addEnvironment(
      "DESTINATION_QUEUE_URL",
      sendNotificationLambda.sqsQueue.queueUrl
    )
    table.grantReadData(processNotification.lambdaFunction)

    val ruleDlq = buildDeadLetterQueue(this, "RuleDlq")

    Rule.Builder.create(this, "NewImageRule")
      .description("Filter for newly saved image rule")
      .eventBus(eventBus)
      .eventPattern(
        EventPattern.builder()
          .source(listOf(EventRegistry.EventSource.UsersRepositoryFanout))
          .detailType(listOf(EventRegistry.EventType.DynamodbStreamRecord.EventDetailTypeImageRecordList))
          .build()
      )
      .targets(
        listOf(
          software.amazon.awscdk.services.events.targets.LambdaFunction.Builder.create(
            processNotification.lambdaFunction
          ).deadLetterQueue(ruleDlq)
            .retryAttempts(3)
            .build()
        )
      )
      .build()
  }

  fun createDeviceFunction(): @NotNull Function {
    val deviceToDynamoDB = LambdaToDynamoDB.Builder.create(this, "DeviceLambda")
      .existingTableObj(table)
      .lambdaFunctionProps(
        buildLambdaProps(
          construct = this,
          asset = functionsProp.getProperty("create-device-dynamo"),
          region = region,
          handler = "com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions.OnNewDevice",
          description = "Function for creating Device token to dynamo",
          version = "1.0.0-SNAPSHOT",
          layerId = "CreateDeviceLayerId",
          env = mapOf(
            "APP_NAME" to appName
          )
        )
      )
      .tablePermissions("ReadWrite")
      .build()
    deviceToDynamoDB.lambdaFunction.role?.addManagedPolicy(getLambdaInsightPolicy())

    CfnOutput(
      this,
      "DeviceLambdaOutput",
      CfnOutputProps.builder()
        .value("${deviceToDynamoDB.lambdaFunction.functionName}-${deviceToDynamoDB.lambdaFunction.currentVersion.version}")
        .description("Function for creating Device token to dynamo")
        .build()
    )
    return deviceToDynamoDB.lambdaFunction
  }

  fun createProcessNotificationLambda(sqsQueue: @NotNull Queue): LambdaToSqs {

    val lambdaToSqs = LambdaToSqs.Builder.create(this, "ProcessNotifLambda")
      .existingQueueObj(sqsQueue)
      .lambdaFunctionProps(
        buildLambdaProps(
          construct = this,
          asset = functionsProp.getProperty("process-image-notification"),
          region = region,
          handler = "com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions.OnImageCreated",
          description = "Function for processing notification when a new image has been stored",
          version = "1.0.1-SNAPSHOT",
          layerId = "ProcessNotifLayerId",
          env = mapOf(
            "APP_NAME" to appName,
            "DDB_TABLE_NAME" to table.tableName,
          )
        )
      )
      .build()
    lambdaToSqs.lambdaFunction.role?.addManagedPolicy(getLambdaInsightPolicy())

    CfnOutput(
      this,
      "ProcessNotifLambdaOutput",
      CfnOutputProps.builder()
        .value("${lambdaToSqs.lambdaFunction.functionName}-${lambdaToSqs.lambdaFunction.currentVersion.version}")
        .description("Function for processing notification when a new image has been stored")
        .build()
    )
    return lambdaToSqs
  }

  fun createSendNotificationLambda(): SqsToLambda {

    val secretValue = Secret.fromSecretAttributes(
      this, "GoogleSecret",
      SecretAttributes.builder()
        .secretCompleteArn("arn:aws:secretsmanager:us-east-1:690127176154:secret:socialcats/googleKey-Enx4sx")
        .build()
    ).secretValue
    val sqsTolambda = SqsToLambda.Builder.create(this, "SendNotifLambda")
      .lambdaFunctionProps(
        buildLambdaProps(
          construct = this,
          asset = functionsProp.getProperty("send-notification"),
          region = region,
          handler = "com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions.OnNotification",
          description = "Function for sending notification to FCM",
          version = "1.0.0-SNAPSHOT",
          layerId = "SendNotifLayerId",
          env = mapOf(
            "APP_NAME" to appName,
            "GOOGLE_CREDENTIAL" to secretValue.toString(),
            "DDB_TABLE_NAME" to table.tableName
          )
        )
      )
      .queueProps(
        QueueProps.builder()
          .visibilityTimeout(Duration.seconds(60))
          .build()
      )
      .build()
    sqsTolambda.lambdaFunction.role?.addManagedPolicy(getLambdaInsightPolicy())
    sqsTolambda.lambdaFunction.addEnvironment(
      "NOTIFICATION_QUEUE_URL",
      sqsTolambda.sqsQueue.queueUrl
    )

    CfnOutput(
      this,
      "SendNotifLambdaOutput",
      CfnOutputProps.builder()
        .value("${sqsTolambda.lambdaFunction.functionName}-${sqsTolambda.lambdaFunction.currentVersion.version}")
        .description("Function for sending notification to FCM")
        .build()
    )
    return sqsTolambda
  }
}
