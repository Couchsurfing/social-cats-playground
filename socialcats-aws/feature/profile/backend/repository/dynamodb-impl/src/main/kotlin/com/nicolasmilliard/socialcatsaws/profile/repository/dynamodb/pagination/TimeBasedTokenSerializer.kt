package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.pagination

import com.nicolasmilliard.socialcatsaws.profile.repository.InvalidTokenException
import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.TokenSerializer
import java.time.Duration
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * Implementation of [TokenSerializer] that adds time information
 * to ensure the token will expire.
 */
public class TimeBasedTokenSerializer(private val ttl: Duration) : TokenSerializer<String> {

  override fun deserialize(token: String): String {
    validateTimestamp(token)
    val decodedToken: String = token.substringBeforeLast(TIMESTAMP_DEMILITER)
    if (decodedToken.isBlank()) {
      throw InvalidTokenException("The token is blank.")
    }
    return decodedToken
  }

  override fun serialize(token: String): String {
    val tokenBuilder = StringBuilder(token)
    tokenBuilder.append(TIMESTAMP_DEMILITER)
    tokenBuilder.append(Instant.now().toString())
    return tokenBuilder.toString()
  }

  private fun validateTimestamp(tokenString: String) {
    val timestampString: String = tokenString.substringAfterLast(TIMESTAMP_DEMILITER)
    val timestamp: Instant = try {
      Instant.parse(timestampString)
    } catch (e: DateTimeParseException) {
      throw InvalidTokenException(
        String.format(
          "Invalid timestamp string %s in token.",
          timestampString
        ),
        e
      )
    }
    if (timestamp.plus(ttl).isBefore(Instant.now())) {
      throw InvalidTokenException(
        String.format(
          "Token %s has expired after timeout limit %s.",
          timestamp,
          ttl
        )
      )
    }
  }

  private companion object {
    const val TIMESTAMP_DEMILITER = "&"
  }
}
