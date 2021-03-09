package com.nicolasmilliard.socialcatsaws.backend.core

import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.RemovalPolicy
import software.amazon.awscdk.services.apigatewayv2.CfnStage
import software.amazon.awscdk.services.apigatewayv2.HttpApi
import software.amazon.awscdk.services.apigatewayv2.IHttpRouteAuthorizer
import software.amazon.awscdk.services.apigatewayv2.authorizers.HttpJwtAuthorizer
import software.amazon.awscdk.services.logs.LogGroup
import software.amazon.awscdk.services.logs.LogGroupProps
import software.amazon.awscdk.services.logs.RetentionDays

class Api constructor(
  scope: Construct,
  id: String,
  isProd: Boolean,
  throttlingBurstLimit: Int,
  throttlingRateLimit: Int,
  jwtIssuer: String,
  jwtAudiences: List<String>
) :
  Construct(scope, id) {

  val httpApi: HttpApi
  val authorizer: IHttpRouteAuthorizer

  init {

    httpApi = createHttpApi(isProd, throttlingBurstLimit, throttlingRateLimit)

    // issue Invalid request input. CreateAuthorizer input is missing authorizer type. (Service: AmazonApiGatewayV2; Status Code: 400; Error Code: BadRequestException;
    authorizer = createAuthorizer(jwtIssuer, jwtAudiences)
  }

  private fun createAuthorizer(
    jwtIssuer: String,
    jwtAudiences: List<String>
  ): HttpJwtAuthorizer {
    return HttpJwtAuthorizer.Builder.create()
      .authorizerName("CognitoJwtAuthorizer")
      .identitySource(listOf("\$request.header.Authorization"))
      .jwtAudience(jwtAudiences)
      .jwtIssuer(jwtIssuer)
      .build()
  }

  private fun createHttpApi(isProd: Boolean, throttlingBurstLimit: Int, throttlingRateLimit: Int): HttpApi {
    val removalPolicy = if (isProd) RemovalPolicy.RETAIN else RemovalPolicy.DESTROY

    val api = HttpApi.Builder.create(this, "HttpApi")
      .apiName("Social cats API")
      .createDefaultStage(false)
      .build()

    val logGroup = LogGroup(
      this, "HttpApiLogGroup",
      LogGroupProps.builder()
        .retention(RetentionDays.ONE_MONTH)
        .logGroupName("http-api-log-group")
        .removalPolicy(removalPolicy)
        .build()
    )

    // HttpStage.Builder.create(this, "DefaultStage")
    //     .stageName("\$default")
    //     .httpApi(api)
    //     .autoDeploy(true)
    //     .
    //     .build()
    CfnStage.Builder.create(this, "DefaultStage")
      .apiId(api.httpApiId)
      .stageName("\$default")
      .autoDeploy(true)
      .accessLogSettings(
        CfnStage.AccessLogSettingsProperty.builder()
          .destinationArn(logGroup.logGroupArn)
          .format(
            """
            { "requestId":"#context.requestId", "ip": "\#context.identity.sourceIp", "requestTime":"#context.requestTime", "httpMethod":"#context.httpMethod","routeKey":"#context.routeKey", "status":"#context.status","protocol":"#context.protocol", "responseLength":"#context.responseLength" }
            """.trimIndent().replace('#', '$')
          )
          .build()
      )
      .defaultRouteSettings(
        CfnStage.RouteSettingsProperty.builder()
          .throttlingBurstLimit(throttlingBurstLimit)
          .throttlingRateLimit(throttlingRateLimit)
          .detailedMetricsEnabled(true)
          .build()
      )
      .build()

    // CfnOutput(
    //     this,
    //     "HttpApiStageId",
    //     CfnOutputProps.builder()
    //         .value(defaultStage.apiId)
    //         .description("Cognito User Pool Id")
    //         .build()
    // )
    return api
  }
}
