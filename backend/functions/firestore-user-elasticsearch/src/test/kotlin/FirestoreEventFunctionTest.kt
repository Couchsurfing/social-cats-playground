package com.nicolasmilliard.socialcats

import com.google.cloud.functions.Context
import com.google.common.truth.Truth.assertThat
import com.google.gson.JsonParser
import com.nicolasmilliard.socialcats.model.SearchUsersResult
import com.nicolasmilliard.socialcats.search.SearchUseCase
import com.nicolasmilliard.socialcats.search.repository.SearchRepository
import mu.KotlinLogging
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.junit.Test
import java.util.*

private val log = KotlinLogging.logger {}

internal class FirestoreEventFunctionTest {

    @Test
    fun `on user name updated`() {
        val event = RawFirestoreEvent(
            oldValue = RawFirestoreValue(
                Date(123),
                Date(1234),
                "name/id",
                JsonParser().parse("{\"name\":{\"stringValue\":\"OldName\"}}")
            ),
            value = RawFirestoreValue(
                Date(123),
                Date(12345),
                "name/id",
                JsonParser().parse("{\"name\":{\"stringValue\":\"NewName\"}}")
            ),
            updateMask = RawUpdateMask(setOf("name"))
        )

        val context = FakeContext()
        val fakeSearchUseCase = FakeSearchRepository()
        val searchUseCase = SearchUseCase(fakeSearchUseCase)
        val graph =
            Graph(
                searchUseCase,
                RestHighLevelClient(RestClient.builder(HttpHost.create("http://test.com")))
            )

        FirestoreEventFunction(graph).onUserWritten(event, context)

        assertThat(fakeSearchUseCase.wasUpdatedCalled).isTrue()
    }

    @Test
    fun `on field updated other than name`() {
        val event = RawFirestoreEvent(
            oldValue = RawFirestoreValue(
                Date(123),
                Date(12345),
                "name/id",
                JsonParser().parse("{\"name\":{\"stringValue\":\"OldName\"}}")
            ),
            value = RawFirestoreValue(
                Date(123),
                Date(123456),
                "name/id",
                JsonParser().parse("{\"name\":{\"stringValue\":\"NewName\"}}")
            ),
            updateMask = RawUpdateMask(setOf("other"))
        )

        val context = FakeContext()
        val fakeSearchUseCase = FakeSearchRepository()
        val searchUseCase = SearchUseCase(fakeSearchUseCase)
        val graph =
            Graph(
                searchUseCase,
                RestHighLevelClient(RestClient.builder(HttpHost.create("http://test.com")))
            )

        FirestoreEventFunction(graph).onUserWritten(event, context)

        assertThat(fakeSearchUseCase.wasUpdatedCalled).isFalse()
    }

    @Test
    fun `on new user created`() {
        val event = RawFirestoreEvent(
            oldValue = RawFirestoreValue(
                null,
                null,
                null,
                null
            ),
            value = RawFirestoreValue(
                Date(123),
                Date(123),
                "name/id",
                JsonParser().parse("{\"name\":{\"stringValue\":\"NewName\"}}")
            ),
            updateMask = null
        )

        val context = FakeContext()
        val fakeSearchUseCase = FakeSearchRepository()
        val searchUseCase = SearchUseCase(fakeSearchUseCase)
        val graph =
            Graph(
                searchUseCase,
                RestHighLevelClient(RestClient.builder(HttpHost.create("http://test.com")))
            )

        FirestoreEventFunction(graph).onUserWritten(event, context)

        assertThat(fakeSearchUseCase.wasUpdatedCalled).isTrue()
    }

    @Test
    fun `on new user deleted`() {
        val event = RawFirestoreEvent(
            oldValue = RawFirestoreValue(
                Date(123),
                Date(12345),
                "name/id",
                JsonParser().parse("{\"name\":{\"stringValue\":\"OldName\"}}")
            ),
            value = RawFirestoreValue(
                null,
                null,
                null,
                null
            ),
            updateMask = null
        )

        val context = FakeContext()
        val fakeSearchUseCase = FakeSearchRepository()
        val searchUseCase = SearchUseCase(fakeSearchUseCase)
        val graph =
            Graph(
                searchUseCase,
                RestHighLevelClient(RestClient.builder(HttpHost.create("http://test.com")))
            )

        FirestoreEventFunction(graph).onUserWritten(event, context)

        assertThat(fakeSearchUseCase.wasDeletedCalled).isTrue()
    }

    class FakeContext : Context {
        override fun timestamp() = "timestamp"

        override fun eventId() = "eventId"

        override fun resource() = "resource"

        override fun eventType() = "eventType"
    }

    class FakeSearchRepository: SearchRepository {
        override fun searchUsers(input: String): SearchUsersResult {
            TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
        }

        var wasDeletedCalled: Boolean = false
        override fun deleteUser(id: String) {
            log.debug { "deleteUser()" }
            if (wasDeletedCalled) IllegalStateException("updateUserName called more than once")
            wasDeletedCalled = true
        }

        var wasUpdatedCalled: Boolean = false
        override fun updateUserName(id: String, name: Date, s: String) {
            log.debug { "updateUserName()" }
            if (wasUpdatedCalled) IllegalStateException("updateUserName called more than once")
            wasUpdatedCalled = true
        }
    }
}
