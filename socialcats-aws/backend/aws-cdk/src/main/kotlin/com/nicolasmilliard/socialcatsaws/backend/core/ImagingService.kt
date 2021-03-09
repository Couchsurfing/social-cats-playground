package com.nicolasmilliard.socialcatsaws.backend.core

import com.nicolasmilliard.socialcatsaws.backend.util.LambdaFunction
import com.nicolasmilliard.socialcatsaws.backend.util.buildLambdaProps
import com.nicolasmilliard.socialcatsaws.backend.util.getLambdaInsightPolicy
import software.amazon.awscdk.cloudformation.include.CfnInclude
import software.amazon.awscdk.core.CfnOutput
import software.amazon.awscdk.core.CfnOutputProps
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Duration
import software.amazon.awscdk.core.RemovalPolicy
import software.amazon.awscdk.services.apigateway.CfnDeployment
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.s3.Bucket
import software.amazon.awscdk.services.s3.BucketProps
import software.amazon.awscdk.services.s3.CfnBucket
import software.amazon.awscdk.services.s3.LifecycleRule
import software.amazon.awsconstructs.services.lambdas3.LambdaToS3
import java.util.Properties

class ImagingService(
  scope: Construct,
  id: String,
  functionsProp: Properties,
  isProd: Boolean,
  appName: String,
  region: String,
  table: Table,
  throttlingBurstLimit: Int,
  throttlingRateLimit: Int
) :
  Construct(scope, id) {

  val createUploadUrlFunction: LambdaFunction
  val s3ImageBucket: Bucket
  val s3ImageAccessLogBucket: Bucket

  init {

    val lambdaS3 = LambdaToS3.Builder.create(this, "ImagingS3")
      .bucketPermissions(listOf("Put"))
      .bucketProps(getS3BucketProp(isProd))
      .lambdaFunctionProps(
        buildLambdaProps(
          construct = this,
          asset = functionsProp.getProperty("image-upload-url"),
          region = region,
          handler = "com.nicolasmilliard.socialcatsaws.imageprocessing.backend.functions.GetImageUploadUrl",
          description = "Function for generating pre-signed S3 url for upload.",
          version = "1.0.25-SNAPSHOT",
          layerId = "LambdaToS3LayerId",
          env = mapOf(
            "DDB_TABLE_NAME" to table.tableName,
            "APP_NAME" to appName,
          )
        )
      )
      .build()

    createUploadUrlFunction = lambdaS3.lambdaFunction
    createUploadUrlFunction.role?.addManagedPolicy(getLambdaInsightPolicy())

    table.grantReadData(createUploadUrlFunction)

    CfnOutput(
      this,
      "GetUploadUrlFunctionOutput",
      CfnOutputProps.builder()
        .value("${createUploadUrlFunction.functionName}-${createUploadUrlFunction.currentVersion.version}")
        .description("Function use to generate pre-signed upload S3 Url")
        .build()
    )
    s3ImageBucket = lambdaS3.s3Bucket!!
    s3ImageAccessLogBucket = lambdaS3.s3LoggingBucket!!
    CfnOutput(
      this,
      "ImageBucketNameOutput",
      CfnOutputProps.builder()
        .value(s3ImageBucket.bucketArn)
        .description("S3 Bucket use to store user uploaded images")
        .build()
    )

    val template = CfnInclude.Builder.create(this, "ImageHandlerTemplate")
      .templateFile("src/main/resources/serverless-image-handler-5.2.0.template")
      .preserveLogicalIds(false)
      .parameters(
        mapOf(
          "SourceBuckets" to s3ImageBucket.bucketName,
          "AutoWebP" to "Yes",
          "LogRetentionPeriod" to "1",
          "DeployDemoUI" to if (isProd) "No" else "Yes",
          "EnableSignature" to "No",
          "SecretsManagerSecret" to "",
          "SecretsManagerKey" to "",
          "EnableDefaultFallbackImage" to "No",
          "FallbackImageS3Bucket" to "",
          "FallbackImageS3Key" to "",
          "CorsEnabled" to "No",
          "CorsOrigin" to "",
        )
      )
      .build()

    val cfnDeployment = template.getResource("ImageHandlerApiDeployment") as CfnDeployment
    cfnDeployment.addPropertyOverride(
      "StageDescription.ThrottlingBurstLimit",
      throttlingBurstLimit
    )
    cfnDeployment.addPropertyOverride(
      "StageDescription.ThrottlingRateLimit",
      throttlingRateLimit
    )

    val logsBucket = template.getResource("Logs") as CfnBucket
    logsBucket.applyRemovalPolicy(if (isProd) RemovalPolicy.RETAIN else RemovalPolicy.DESTROY)

    val demoBucket = template.getResource("DemoBucket") as CfnBucket
    demoBucket.applyRemovalPolicy(if (isProd) RemovalPolicy.RETAIN else RemovalPolicy.DESTROY)
  }

  private fun getS3BucketProp(isProd: Boolean): BucketProps {
    val removalPolicy = if (isProd) RemovalPolicy.RETAIN else RemovalPolicy.DESTROY
    return BucketProps.builder()
      .versioned(false)
      .lifecycleRules(
        listOf(
          LifecycleRule.builder()
            .abortIncompleteMultipartUploadAfter(Duration.days(1))
            .build()
        )
      )
      .removalPolicy(removalPolicy)
      .autoDeleteObjects(!isProd)
      .build()
  }
}
