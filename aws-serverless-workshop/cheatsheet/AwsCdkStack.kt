package com.myorg

import com.nicolasmilliard.serverlessworkshop.Schema
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Duration
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

class AwsCdkStack(
    parent: Construct,
    id: String,
    props: StackProps? = null
) : Stack(parent, id, props) {
    init {
        val messagingTable = Table.Builder.create(this, "MessagingTable")
            .tableName(Schema.TABLE_NAME)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .encryption(TableEncryption.AWS_MANAGED)
            .partitionKey(Attribute.builder().name(Schema.SharedAttributes.PARTITION_KEY).type(AttributeType.STRING).build())
            .sortKey(Attribute.builder().name(Schema.SharedAttributes.SORT_KEY).type(AttributeType.STRING).build())
            .build()

        val createMessagingLambda = Function.Builder.create(this, "MessagingLambda")
            .description("Function handling CRUD conversation operations")
            .code(Code.fromAsset("../messaging-lambda/build/libs/messaging-lambda-all.jar"))
            .handler("com.nicolasmilliard.serverlessworkshop.messaging.CrudConversationHandler")
            .runtime(Runtime.JAVA_11)
            .timeout(Duration.seconds(30))
            .memorySize(512)
            .build()

        messagingTable.grantWriteData(createMessagingLambda)

        val httpApi = HttpApi.Builder.create(this, "HttpApi")
            .apiName("Messaging API")
            .build()

        val integration = LambdaProxyIntegration.Builder.create()
            .handler(createMessagingLambda)
            .payloadFormatVersion(PayloadFormatVersion.VERSION_2_0)
            .build()

        httpApi.addRoutes(
            AddRoutesOptions.builder()
                .path("/v1/conversations")
                .methods(listOf(HttpMethod.ANY))
                .integration(integration)
                .build()
        )
    }
}