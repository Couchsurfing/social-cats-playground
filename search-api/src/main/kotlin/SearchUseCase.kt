package com.nicolasmilliard.socialcats

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import mu.KotlinLogging
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.VersionType
import org.elasticsearch.index.mapper.MapperService
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import java.util.*

private val log = KotlinLogging.logger {}

interface SearchUseCase {
  fun updateUserName(id: String, updateTime: Date, name: String)
  fun searchUsers(input: String): UsersSearchResponse
  fun deleteUser(id: String)
}

@JsonClass(generateAdapter = true)
data class User(
  val name: String
)

@JsonClass(generateAdapter = true)
data class UsersSearchResponse(
  val totalHits: Long,
  val users: List<User>
)

class ElasticSearchUseCase(
  private val esClient: RestHighLevelClient,
  private val moshi: Moshi
) : SearchUseCase {

  private val index = "users-index"
  private val type = MapperService.SINGLE_MAPPING_NAME

  override fun updateUserName(id: String, updateTime: Date, name: String) =
    measureTimeMillis({ log.debug { "Updating name took $it ms" } }) {
      log.info { "Updating User name, id: $id" }
      // Create the document as a hash map
      val document = mapOf(
        "name" to name
      )

      // Form the indexing request, send it, and print the response
      val request = IndexRequest(index, type, id)
        .versionType(VersionType.EXTERNAL)
        .version(updateTime.time)
        .source(document)

      val response = esClient.index(request, RequestOptions.DEFAULT)
      log.debug { "Response: $response" }
    }

  override fun deleteUser(id: String) =
    measureTimeMillis({ log.debug { "Deleting user took $it ms" } }) {
      log.info { "Deleting User, id: $id" }
      val request = DeleteRequest(index, type, id)

      val response = esClient.delete(request, RequestOptions.DEFAULT)
      log.debug { "Response: $response" }
    }

  override fun searchUsers(input: String): UsersSearchResponse =
    measureTimeMillis({ log.debug { "Search user took $it ms" } }) {
      log.info { "Searching Users, input: $input" }
      val searchSourceBuilder = SearchSourceBuilder()
        .apply {
          if (input.isNullOrBlank()) {
            query(QueryBuilders.matchAllQuery())
          } else {
            query(QueryBuilders.matchQuery("name", input))
          }
        }

      val searchRequest = SearchRequest(index).source(searchSourceBuilder)
      val response = esClient.search(searchRequest, RequestOptions.DEFAULT)
      log.debug { "Response: $response" }

      val adapter = moshi.adapter(User::class.java)
      val hitCount = response.hits.totalHits

      return UsersSearchResponse(
        hitCount,
        response.hits.map { adapter.fromJson(it.sourceAsString)!! })
    }
}
