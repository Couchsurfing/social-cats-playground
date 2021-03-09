package com.myorg


import com.nicolasmilliard.serverlessworkshop.Schema
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Duration
import software.amazon.awscdk.core.RemovalPolicy
import software.amazon.awscdk.core.Stack
import software.amazon.awscdk.core.StackProps
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions
import software.amazon.awscdk.services.apigatewayv2.HttpApi
import software.amazon.awscdk.services.apigatewayv2.HttpMethod
import software.amazon.awscdk.services.apigatewayv2.PayloadFormatVersion
import software.amazon.awscdk.services.apigatewayv2.integrations.LambdaProxyIntegration
import software.amazon.awscdk.services.dynamodb.Attribute
import software.amazon.awscdk.services.dynamodb.AttributeType
import software.amazon.awscdk.services.dynamodb.BillingMode
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.dynamodb.TableEncryption
import software.amazon.awscdk.services.lambda.Code
import software.amazon.awscdk.services.lambda.Function
import software.amazon.awscdk.services.lambda.Runtime
import software.amazon.awscdk.services.lambda.VersionOptions


class AwsCdkStack @JvmOverloads constructor(parent: Construct?, id: String?, props: StackProps? = null, isProd:Boolean) :
    Stack(parent, id, props) {
    init {
        val table = Table.Builder.create(this, "DynamoTable")
            .tableName(Schema.TABLE_NAME)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .encryption(TableEncryption.AWS_MANAGED)
            .pointInTimeRecovery(true)
            .partitionKey(
                Attribute.builder()
                    .type(AttributeType.STRING)
                    .name(Schema.SharedAttributes.PARTITION_KEY)
                    .build()
            )
            .sortKey(
                Attribute.builder()
                    .type(AttributeType.STRING)
                    .name(Schema.SharedAttributes.SORT_KEY)
                    .build()
            )
            .removalPolicy(if (isProd)RemovalPolicy.RETAIN else RemovalPolicy.DESTROY)
            .build()

        val lambda = Function.Builder.create(this, "LambdaFunction")
            .description("Lambda function for CRUD messaging operation.")
            .code(Code.fromAsset("../messaging-lambda/build/libs/messaging-lambda-all.jar"))
            .handler("com.nicolasmilliard.serverlessworkshop.messaging.CrudConversationHandler")
            .runtime(Runtime.JAVA_11)
            .timeout(Duration.seconds(30))
            .memorySize(512)
            .currentVersionOptions(VersionOptions.builder()
                .description("1.0.0").build())
            .build()

        table.grantReadWriteData(lambda)

        val httpApi = HttpApi.Builder.create(this, "HttpApi")
            .apiName("Messaging API")
            .build()

        val lambdaIntegration = LambdaProxyIntegration.Builder.create()
            .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
            .handler(lambda)
            .build()

        httpApi.addRoutes(AddRoutesOptions.builder()
            .path("/v1/conversations")
            .methods(listOf(HttpMethod.ANY))
            .integration(lambdaIntegration)
            .build())
    }
}