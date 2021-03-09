package com.nicolasmilliard.socialcatsaws.backend.core

import software.amazon.awscdk.core.CfnOutput
import software.amazon.awscdk.core.CfnOutputProps
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.core.Duration
import software.amazon.awscdk.services.events.Archive
import software.amazon.awscdk.services.events.EventBus
import software.amazon.awscdk.services.events.EventBusProps
import software.amazon.awscdk.services.events.EventPattern

class EventBusService(
  scope: Construct,
  id: String
) :
  Construct(scope, id) {

  val eventBus: EventBus

  init {

    eventBus = EventBus(
      this, "EventBus",
      EventBusProps.builder()
        .build()
    )

    Archive.Builder.create(this, "Archive")
      .description("Archive all event for 10 days")
      .sourceEventBus(eventBus)
      .retention(Duration.days(10))
      .eventPattern(EventPattern.builder().build())
      .build()
    CfnOutput(
      this,
      "EventBusNameOutput",
      CfnOutputProps.builder()
        .value("${eventBus.eventBusName}")
        .description("Default Event bus")
        .build()
    )
  }
}
