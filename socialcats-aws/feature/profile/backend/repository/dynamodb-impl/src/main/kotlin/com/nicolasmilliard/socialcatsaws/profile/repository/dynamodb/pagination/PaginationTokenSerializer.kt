package com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.pagination

import com.nicolasmilliard.socialcatsaws.profile.repository.dynamodb.TokenSerializer
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.kms.KmsClient
import java.time.Duration

/**
 * Implementation of [TokenSerializer] to serialize/deserialize
 * pagination token for List APIs.
 *
 *
 * It chians [DynamoDbStartKeySerializer], [TimeBasedTokenSerializer]
 * and [EncryptedTokenSerializer] to serialize/deserialize a DynamoDb start key
 * to a String token that is URL friendly.
 *
 *
 * Serialize flow:
 * DynamoDb start key -> Json string -> Json String with TTL -> base64 encoded cipher text (Token)
 *
 *
 * Deserialize flow:
 * Token -> Base64 decoded plaintext -> Json String with TTL -> Json String -> DynamoDb start key
 */
public class PaginationTokenSerializer(kms: KmsClient, paginationTokenTtl: Duration, kmsKeyId: String) :
  TokenSerializer<Map<String, AttributeValue>> {
  private val dynamoDbStartKeySerializer: TokenSerializer<Map<String, AttributeValue>>
  private val timeBasedTokenSerializer: TokenSerializer<String>
  private val encryptedTokenSerializer: TokenSerializer<String>
  /**
   * Construct PaginationTokenSerializer from KmsClient and ConfigProvider.
   *
   * @param kms            KmsClient for token encryption and decryption.
   * @param configProvider ConfigProvider to provide configuration values.
   */
  init {
    dynamoDbStartKeySerializer = DynamoDbStartKeySerializer()
    timeBasedTokenSerializer = TimeBasedTokenSerializer(
      paginationTokenTtl
    )
    encryptedTokenSerializer = EncryptedTokenSerializer(
      kms, kmsKeyId
    )
  }

  override fun deserialize(token: String): Map<String, AttributeValue> {
    val plaintext = encryptedTokenSerializer.deserialize(token)
    val json = timeBasedTokenSerializer.deserialize(plaintext)
    return dynamoDbStartKeySerializer.deserialize(json)
  }

  override fun serialize(token: Map<String, AttributeValue>): String {
    val json = dynamoDbStartKeySerializer.serialize(token)
    val jsonWithTtl = timeBasedTokenSerializer.serialize(json)
    return encryptedTokenSerializer.serialize(jsonWithTtl)
  }
}
