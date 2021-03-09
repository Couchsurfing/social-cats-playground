package com.nicolasmilliard.socialcatsaws.backend.util

import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Duration
import software.amazon.awscdk.core.RemovalPolicy
import software.amazon.awscdk.core.Stack
import software.amazon.awscdk.services.iam.AccountPrincipal
import software.amazon.awscdk.services.iam.AnyPrincipal
import software.amazon.awscdk.services.iam.Effect
import software.amazon.awscdk.services.iam.IManagedPolicy
import software.amazon.awscdk.services.iam.ManagedPolicy
import software.amazon.awscdk.services.iam.PolicyStatement
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.FunctionProps
import software.amazon.awscdk.services.lambda.IDestination
import software.amazon.awscdk.services.lambda.ILayerVersion
import software.amazon.awscdk.services.lambda.LayerVersion
import software.amazon.awscdk.services.lambda.Runtime
import software.amazon.awscdk.services.lambda.VersionOptions
import software.amazon.awscdk.services.sqs.IQueue
import software.amazon.awscdk.services.sqs.Queue
import software.amazon.awscdk.services.sqs.QueueEncryption

fun getLambdaInsightPolicy(): IManagedPolicy {
  return ManagedPolicy.fromAwsManagedPolicyName("CloudWatchLambdaInsightsExecutionRolePolicy")
}

private fun getCloudWatchLambdaInsightLayerVersion(construct: Construct, region: String, layerId: String): ILayerVersion {
  return LayerVersion.fromLayerVersionArn(
    construct,
    layerId,
    "arn:aws:lambda:$region:580247275435:layer:LambdaInsightsExtension:12"
  )
}

fun buildLambdaProps(
  construct: Construct,
  asset: String,
  region: String,
  handler: String,
  description: String,
  version: String,
  layerId: String,
  env: Map<String, String> = emptyMap(),
  onSuccess: IDestination? = null,
  onFailure: IDestination? = null
): FunctionProps {
  val layerVersion =
    getCloudWatchLambdaInsightLayerVersion(construct, region, layerId)
  return FunctionProps.builder()
    .code(Code.fromAsset(asset))
    .handler(handler)
    .description(description)
    .runtime(Runtime.JAVA_11)
    .timeout(Duration.seconds(30))
    .memorySize(512)
    .layers(listOf(layerVersion))
    .environment(env)
    .currentVersionOptions(
      VersionOptions.builder()
        .description(version)
        .removalPolicy(RemovalPolicy.DESTROY)
        .build()
    )
    .onSuccess(onSuccess)
    .onFailure(onFailure)
    .build()
}

fun buildDeadLetterQueue(construct: Construct, id: String): IQueue {

  val queue = Queue.Builder.create(construct, id)
    .encryption(QueueEncryption.KMS_MANAGED)
    .retentionPeriod(Duration.days(14))
    .build()

  applySecureQueuePolicy(construct, queue)

  return queue
}

fun applySecureQueuePolicy(construct: Construct, queue: Queue) {
  queue.addToResourcePolicy(
    PolicyStatement.Builder.create()
      .sid("QueueOwnerOnlyAccess")
      .resources(listOf(queue.queueArn))
      .actions(
        listOf(
          "sqs:DeleteMessage",
          "sqs:ReceiveMessage",
          "sqs:SendMessage",
          "sqs:GetQueueAttributes",
          "sqs:RemovePermission",
          "sqs:AddPermission",
          "sqs:SetQueueAttributes"
        )
      )
      .principals(listOf(AccountPrincipal(Stack.of(construct).account)))
      .effect(Effect.ALLOW)
      .build()
  )

  queue.addToResourcePolicy(
    PolicyStatement.Builder.create()
      .sid("HttpsOnly")
      .resources(listOf(queue.queueArn))
      .actions(listOf("SQS:*"))
      .principals(listOf(AnyPrincipal()))
      .effect(Effect.DENY)
      .conditions(mapOf("Bool" to mapOf("aws:SecureTransport" to false)))
      .build()
  )
}
