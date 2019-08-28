package com.nicolasmilliard.socialcats.search

import com.nicolasmilliard.presentation.Presenter
import com.nicolasmilliard.socialcats.auth.Auth
import com.nicolasmilliard.socialcats.model.User
import com.nicolasmilliard.socialcats.search.SearchPresenter.Event
import com.nicolasmilliard.socialcats.search.SearchPresenter.Model
import com.nicolasmilliard.socialcats.search.SearchPresenter.Model.LoadingStatus.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class SearchPresenter(
    private val auth: Auth,
    private val searchLoader: SearchLoader
) : Presenter<Model, Event> {

    private val _models = ConflatedBroadcastChannel<Model>()
    override val models: ReceiveChannel<Model> get() = _models.openSubscription()

    private val _events = Channel<Event>(RENDEZVOUS)
    override val events: SendChannel<Event> get() = _events

    override suspend fun start() {
        logger.info { "start" }
        coroutineScope {
            var model = Model()
            fun sendModel(newModel: Model) {
                model = newModel
                _models.offer(newModel)
            }

            launch {
                var activeQuery = ""
                var activeQueryJob: Job? = null

                _events.consumeEach {
                    when (it) {
                        is Event.ClearSyncStatus -> {
                            sendModel(model.copy(loadingStatus = IDLE))
                        }
                        is Event.QueryChanged -> {
                            val query = it.query
                            if (query != activeQuery) {
                                activeQuery = query
                                activeQueryJob?.cancel()
                                var authToken = auth.getIdToken()
                                if (authToken == null) {
                                    sendModel(
                                        model.copy(
                                            queryResults = Model.QueryResults(
                                                "",
                                                emptyList()
                                            )
                                        )
                                    )
                                } else {
                                    activeQueryJob = launch {

                                        searchLoader.searchUsers(authToken, query)
                                            .collect { result ->
                                                sendModel(
                                                    when (result) {
                                                        SearchLoader.Status.InProgress -> model.copy(
                                                            loadingStatus = LOADING
                                                        )
                                                        is SearchLoader.Status.Success -> model.copy(
                                                            loadingStatus = IDLE,
                                                            count = result.data.totalHits,
                                                            queryResults = Model.QueryResults(
                                                                activeQuery,
                                                                result.data.users
                                                            )
                                                        )
                                                        is SearchLoader.Status.Failure -> model.copy(
                                                            loadingStatus = FAILED
                                                        )
                                                    }
                                                )
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    sealed class Event {
        data class QueryChanged(val query: String) : Event()
        object ClearSyncStatus : Event()
    }

    data class Model(
        val count: Long = 0,
        val queryResults: QueryResults = QueryResults(),
        val loadingStatus: LoadingStatus = IDLE
    ) {
        data class QueryResults(
            val query: String = "",
            val items: List<User> = emptyList()
        )

        enum class LoadingStatus {
            IDLE, LOADING, FAILED
        }
    }
}
