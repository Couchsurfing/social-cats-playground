package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.pagination.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import software.amazon.awssdk.core.SdkBytes
import java.nio.charset.Charset

/**
 * Jackson Json Serializer for [SdkBytes].
 */
internal class SdkBytesSerializer : JsonSerializer<SdkBytes>() {
  private val charset: Charset = Charset.defaultCharset()

  override fun serialize(
    sdkBytes: SdkBytes,
    jsonGenerator: JsonGenerator,
    serializerProvider: SerializerProvider
  ) {
    jsonGenerator.writeString(sdkBytes.asString(charset))
  }
}
