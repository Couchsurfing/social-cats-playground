package com.nicolasmilliard.socialcatsaws.backend.core

import software.amazon.awscdk.core.CfnOutput
import software.amazon.awscdk.core.CfnOutputProps
import software.amazon.awscdk.core.Construct
import software.amazon.awscdk.services.events.EventBus
import software.amazon.awscdk.services.events.EventBusProps

class EventBusService(
  scope: Construct,
  id: String
) :
  Construct(scope, id) {

  val eventBus: EventBus

  init {

    eventBus = EventBus(this, "EventBus", EventBusProps.builder()
      .build())

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
