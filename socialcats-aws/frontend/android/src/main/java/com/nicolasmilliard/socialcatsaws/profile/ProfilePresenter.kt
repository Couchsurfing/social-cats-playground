package com.nicolasmilliard.socialcatsaws.profile

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.RENDEZVOUS
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

class ProfilePresenter @ViewModelInject constructor(
  @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
  private val _models = MutableStateFlow(Model())
  val models: StateFlow<Model> get() = _models

  private val _events = Channel<Event>(RENDEZVOUS)
  val events: (Event) -> Unit get() = { _events.offer(it) }

  @OptIn(ExperimentalTime::class)
  fun loadProfile(userId: String) {
    viewModelScope.launch {
      _models.value = Model(true, userId)
      delay(10.seconds)
      _models.value = Model(false, userId, "Nico")
    }
  }

  sealed class Event {
    data class QueryChanged(val query: String) : Event()
    object ClearRefreshStatus : Event()
    object Retry : Event()
  }

  data class Model(
    val isLoading: Boolean = true,
    val userId: String = "",
    val name: String? = null
  )
}
