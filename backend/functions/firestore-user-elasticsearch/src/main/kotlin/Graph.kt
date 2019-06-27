package com.nicolasmilliard.socialcats

import com.amazonaws.http.AWSRequestSigningApacheInterceptor
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.nicolasmilliard.socialcats.search.SearchUseCase
import com.nicolasmilliard.socialcats.search.repository.ElasticSearchRepository
import org.apache.http.HttpHost
import org.apache.http.HttpRequestInterceptor
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.auth.signer.Aws4Signer
import software.amazon.awssdk.auth.signer.params.Aws4SignerParams
import software.amazon.awssdk.regions.Region

data class Graph(
    val searchUseCase: SearchUseCase,
    val esClient: RestHighLevelClient
)

class AppComponent(
    val module: AppModule = AppModule()
) {
    fun build(): Graph {
//        val firebaseOptions = module.provideFirebaseOptions()
//        Firebase.initializeApp(firebaseOptions)
//        val firestore = module.provideFirestore()
        val interceptor = module.provideAwsInterceptor()
        val esClient = module.provideEsClient(interceptor)
        val searchRepository = module.provideSearchRepository(esClient)
        val searchUseCase = module.provideSearchUseCase(searchRepository)
        return Graph(searchUseCase, esClient)
    }
}

class AppModule {

    fun provideFirebaseOptions(): FirebaseOptions {
        val credentials = GoogleCredentials.getApplicationDefault()
        return FirebaseOptions.Builder()
            .setCredentials(credentials)
            .setProjectId("sweat-monkey")
            .build()
    }

    fun provideFirestore(): Firestore {
        return FirestoreClient.getFirestore()
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

        return RestHighLevelClient(RestClient.builder(HttpHost.create(aesEndpoint)).setHttpClientConfigCallback { hacb ->
            hacb.addInterceptorLast(
                interceptor
            )
        })
    }

    fun provideSearchRepository(esClient: RestHighLevelClient) =
        ElasticSearchRepository(esClient)

    fun provideSearchUseCase(repository: ElasticSearchRepository) =
        SearchUseCase(repository)
}
