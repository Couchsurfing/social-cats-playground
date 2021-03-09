package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.pagination.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import software.amazon.awssdk.core.SdkBytes
import java.io.IOException
import java.nio.charset.Charset

/**
 * Jackson Json Deserializer for [SdkBytes].
 */
internal class SdkBytesDeserializer : JsonDeserializer<SdkBytes>() {
  private val charset: Charset = Charset.defaultCharset()

  @Throws(IOException::class, JsonProcessingException::class)
  override fun deserialize(
    jsonParser: JsonParser,
    deserializationContext: DeserializationContext
  ): SdkBytes {
    return SdkBytes.fromString(jsonParser.valueAsString, charset)
  }
}
