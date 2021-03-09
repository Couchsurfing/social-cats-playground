package com.nicolasmilliard.socialcatsaws.eventpublisher

public data class Event(
  public val source: String,
  public val detailType: String,
  public val sourceArns: List<String>,
  public val content: String
)
