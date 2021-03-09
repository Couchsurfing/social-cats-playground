package com.nicolasmilliard.socialcatsaws.profile.repository

/**
 * Exception for invalid token.
 */
public class InvalidTokenException : Exception {
  public constructor(message: String) : super(message)
  public constructor(message: String, exception: Exception) : super(message, exception)
}
