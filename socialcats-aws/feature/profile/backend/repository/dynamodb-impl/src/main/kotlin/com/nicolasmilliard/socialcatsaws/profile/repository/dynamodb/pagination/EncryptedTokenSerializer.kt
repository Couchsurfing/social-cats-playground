package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.pagination

import com.nicolasmilliard.socialcatsaws.profile.repository.InvalidTokenException
import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.TokenSerializer
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.kms.KmsClient
import software.amazon.awssdk.services.kms.model.DecryptRequest
import software.amazon.awssdk.services.kms.model.EncryptRequest
import software.amazon.awssdk.services.kms.model.InvalidCiphertextException
import java.nio.charset.StandardCharsets
import java.util.Base64

/**
 * Implementation of [TokenSerializer] that encrypts and decrypts the token
 * using KMS as well as encode and decode the token using Base64.
 *
 *
 * Note, this class uses KMS to encrypt and decrypt directly. KMS encrypt only allows plain text
 * that is smaller than 4096 bytes. If your plain text is larger than 4096 bytes, use envelope
 * encryption with KMS instead. See doc: https://docs.aws.amazon.com/kms/latest/developerguide/concepts.html#enveloping.
 */
public class EncryptedTokenSerializer(
  private val kms: KmsClient,
  private val keyId: String
) : TokenSerializer<String> {

  @Throws(InvalidTokenException::class)
  override fun deserialize(token: String): String {
    return try {
      kms.decrypt(
        DecryptRequest.builder()
          .ciphertextBlob(SdkBytes.fromByteArray(base64Decode(token)))
          .build()
      )
        .plaintext()
        .asUtf8String()
    } catch (e: InvalidCiphertextException) {
      throw InvalidTokenException("Failed to decrypt token:$token", e)
    }
  }

  override fun serialize(token: String): String {
    val plainText = token.toByteArray(DEFAULT_ENCODING)
    val cipherText: ByteArray = kms.encrypt(
      EncryptRequest.builder()
        .plaintext(SdkBytes.fromByteArray(plainText))
        .keyId(keyId)
        .build()
    )
      .ciphertextBlob()
      .asByteArray()
    return base64Encode(cipherText)
  }

  private fun base64Encode(token: ByteArray): String {
    // Using UrlEncoder to avoid url unfriendly character in next token.
    return String(Base64.getUrlEncoder().encode(token), DEFAULT_ENCODING)
  }

  @Throws(InvalidTokenException::class)
  private fun base64Decode(encodedToken: String): ByteArray {
    if (encodedToken.isBlank()) {
      throw InvalidTokenException("The token is blank.")
    }
    return try {
      Base64.getUrlDecoder().decode(encodedToken.toByteArray(DEFAULT_ENCODING))
    } catch (e: IllegalArgumentException) {
      throw InvalidTokenException(
        String.format(
          "Failed to base64 decode token %s", encodedToken
        ),
        e
      )
    }
  }

  private companion object {
    private val DEFAULT_ENCODING = StandardCharsets.UTF_8
  }
}
