package com.nicolasmilliard.socialcatsaws.eventsource

public interface BatchEventSource<T> {

  public fun markEventAsProcessed(events: List<T>)

  public fun updateEventTimeoutVisibility(events: Map<T, Long?>)
}
