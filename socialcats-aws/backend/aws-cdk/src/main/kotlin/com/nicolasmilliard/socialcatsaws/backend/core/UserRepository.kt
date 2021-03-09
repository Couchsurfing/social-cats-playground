package com.nicolasmilliard.socialcatsaws.backend.core

import com.nicolasmilliard.socialcatsaws.backend.util.buildDeadLetterQueue
import com.nicolasmilliard.socialcatsaws.backend.util.buildLambdaProps
import com.nicolasmilliard.socialcatsaws.backend.util.getLambdaInsightPolicy
import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.Schema
import software.amazon.awscdk.core.CfnOutput
import software.amazon.awscdk.core.CfnOutputProps
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.RemovalPolicy
import software.amazon.awscdk.services.dynamodb.Attribute
import software.amazon.awscdk.services.dynamodb.AttributeType
import software.amazon.awscdk.services.dynamodb.BillingMode
import software.amazon.awscdk.services.dynamodb.GlobalSecondaryIndexProps
import software.amazon.awscdk.services.dynamodb.ProjectionType
import software.amazon.awscdk.services.dynamodb.StreamViewType
import software.amazon.awscdk.services.dynamodb.Table
import software.amazon.awscdk.services.dynamodb.TableEncryption
import software.amazon.awscdk.services.events.EventBus
import software.amazon.awsconstructs.services.dynamodbstreamlambda.DynamoDBStreamToLambda
import software.amazon.awsconstructs.services.dynamodbstreamlambda.DynamoDBStreamToLambdaProps
import java.util.Properties

class UserRepository(
  scope: Construct,
  id: String,
  isProd: Boolean,
  functionsProp: Properties,
  region: String,
  appName: String,
  eventBus: EventBus,
) : Construct(scope, id) {

  val dynamodbTable: Table

  init {
    dynamodbTable = createDynamoTable(isProd)

    val dlq = buildDeadLetterQueue(this, "DLQ")

    val streamToLambda = DynamoDBStreamToLambda(
      this, "DynamoStreamLambda",
      DynamoDBStreamToLambdaProps.builder()
        .deploySqsDlqQueue(true)
        .existingTableObj(dynamodbTable)
        .lambdaFunctionProps(
          buildLambdaProps(
            construct = this,
            asset = functionsProp.getProperty("dynamodb-stream"),
            region = region,
            handler = "com.nicolasmilliard.socialcatsaws.profile.backend.functions.OnDynamoStream",
            description = "Function to fanout Dynamo stream to EventBridge",
            version = "1.0.4-SNAPSHOT",
            layerId = "DynamoToStreamLayerId",
            env = mapOf(
              "APP_NAME" to appName,
              "EVENT_BUS_NAME" to eventBus.eventBusName,
              "MAX_ATTEMPT" to "1",
              "DLQ_URL" to dlq.queueUrl
            ),
          )
        )
        .dynamoEventSourceProps(
          mapOf(
            "retryAttempts" to 5,
            "batchSize" to 10
          )
        )
        .build()
    )
    streamToLambda.lambdaFunction.role?.addManagedPolicy(getLambdaInsightPolicy())

    dlq.grantSendMessages(streamToLambda.lambdaFunction)

    eventBus.grantPutEventsTo(streamToLambda.lambdaFunction)
  }

  private fun createDynamoTable(isProd: Boolean): Table {

    val removalPolicy = if (isProd) RemovalPolicy.RETAIN else RemovalPolicy.DESTROY

    val table = Table.Builder.create(this, "UsersTable")
      .tableName(Schema.TABLE_NAME)
      .partitionKey(
        Attribute.builder()
          .name(Schema.SharedAttributes.PARTITION_KEY)
          .type(AttributeType.STRING)
          .build()
      )
      .sortKey(
        Attribute.builder()
          .name(Schema.SharedAttributes.SORT_KEY)
          .type(AttributeType.STRING)
          .build()
      )
      .stream(StreamViewType.NEW_IMAGE)
      // The default removal policy is RETAIN, which means that cdk destroy will not attempt to delete
      // the new table, and it will remain in your account until manually deleted. By setting the policy to
      // DESTROY, cdk destroy will delete the table (even if it has data in it)
      .removalPolicy(removalPolicy)
      .billingMode(BillingMode.PAY_PER_REQUEST)
      .encryption(TableEncryption.AWS_MANAGED)
      .pointInTimeRecovery(isProd)
      .build()

    table.addGlobalSecondaryIndex(
      GlobalSecondaryIndexProps.builder()
        .indexName(Schema.GSI1_INDEX_NAME)
        .partitionKey(
          Attribute.builder()
            .name(Schema.SharedAttributes.SORT_KEY)
            .type(AttributeType.STRING)
            .build()
        )
        .sortKey(
          Attribute.builder()
            .name(Schema.SharedAttributes.PARTITION_KEY)
            .type(AttributeType.STRING)
            .build()
        )
        .projectionType(ProjectionType.KEYS_ONLY)
        .build()
    )

    CfnOutput(
      this,
      "UserTableOutput",
      CfnOutputProps.builder()
        .value(table.tableName)
        .description("DynamoDB Single table use to store users data")
        .build()
    )
    return table
  }
}
