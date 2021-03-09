package com.nicolasmilliard.socialcatsaws.eventpublisher

public interface BatchEventPublisher {

  public fun publish(events: List<Event>)
}
