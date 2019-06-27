package com.nicolasmilliard.socialcats.searchapi

import com.amazonaws.http.AWSRequestSigningApacheInterceptor
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.nicolasmilliard.socialcats.ElasticSearchUseCase
import com.nicolasmilliard.socialcats.SearchUseCase
import com.ryanharter.ktor.moshi.moshi
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.locations.Locations
import io.ktor.request.path
import io.ktor.routing.routing
import io.ktor.server.engine.ShutDownUrl
import org.apache.http.HttpHost
import org.apache.http.HttpRequestInterceptor
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.slf4j.event.Level
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.auth.signer.Aws4Signer
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams
import software.amazon.awssdk.regions.Region
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

  install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/") }
  }

  install(DefaultHeaders)
  install(ShutDownUrl.ApplicationCallFeature) {
    // The URL that will be intercepted (you can also use the application.conf's ktor.deployment.shutdown.url key)
    shutDownUrl = "/_ah/stop"
    // A function that will be executed to get the exit code of the process
    exitCodeSupplier = { 0 } // ApplicationCall.() -> Int
  }

  initFirebaseApp()
  install(Authentication) {
    firebaseAuth(FirebaseAuthKey) {
      firebaseAuth = FirebaseAuth.getInstance()
      validate = { PrincipalToken(it) }
    }
  }
  install(ContentNegotiation) {
    moshi {
      // Configure the Moshi.Builder here.
      add(Date::class.java, Rfc3339DateJsonAdapter())
    }
  }
  install(Locations)

  val esClient = provideEsClient(provideAwsInterceptor())
  val moshi = provideMoshi()
  val searchUseCase: SearchUseCase = ElasticSearchUseCase(esClient, moshi)

  routing {
    home()
    search()
    dummySearch2(searchUseCase)
  }
}

fun initFirebaseApp() {
  val credentials = GoogleCredentials.getApplicationDefault()
  val options = FirebaseOptions.Builder()
    .setCredentials(credentials)
    .setProjectId("sweat-monkey")
    .build()
  FirebaseApp.initializeApp(options)
}

fun provideAwsInterceptor(): AWSRequestSigningApacheInterceptor {
  val credentialsProvider = EnvironmentVariableCredentialsProvider.create()
  val serviceName = System.getenv("AES_SERVICE_NAME")
  val region = System.getenv("AES_REGION")
  val signer = Aws4Signer.create()
  val aws4SignerParams = Aws4SignerParams.builder()
    .signingName(serviceName)
    .signingRegion(Region.of(region))
    .awsCredentials(credentialsProvider.resolveCredentials()).build()
  return AWSRequestSigningApacheInterceptor(aws4SignerParams, signer)
}

fun provideEsClient(interceptor: HttpRequestInterceptor): RestHighLevelClient {

  val aesEndpoint = System.getenv("AES_ENDPOINT")

  return RestHighLevelClient(
    RestClient.builder(HttpHost.create(aesEndpoint))
      .setHttpClientConfigCallback { hacb ->
        hacb.addInterceptorLast(
          interceptor
        )
      })
}

fun provideMoshi() = Moshi.Builder().build()
