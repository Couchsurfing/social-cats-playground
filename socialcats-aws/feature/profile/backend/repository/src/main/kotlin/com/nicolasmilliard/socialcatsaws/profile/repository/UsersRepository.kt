package com.nicolasmilliard.socialcatsaws.profile.repository

import com.nicolasmilliard.socialcatsaws.profile.model.Device
import com.nicolasmilliard.socialcatsaws.profile.model.Image
import com.nicolasmilliard.socialcatsaws.profile.model.User
import com.nicolasmilliard.socialcatsaws.profile.model.UserWithImages

public interface UsersRepository {
  public fun getUserAndImages(userId: String, newestImagesCount: Int): UserWithImages

  // public fun insertUser(user: User): InsertResult
  public fun updateUser(user: User): User

  public fun getUserById(id: String): User

  public fun insertImage(image: Image): InsertResult
  public fun countImages(userId: String): Int

  public fun insertDevice(device: Device)
  public fun deleteDevices(devices: List<UserDeviceToken>)

  public fun getDeviceTokens(userId: String, limit: Int, pageToken: String?): TokensResult
}

public sealed class InsertResult {
  public object Added : InsertResult()
  public object AlreadyExist : InsertResult()
}

public data class TokensResult(
  val tokens: List<String>,
  val nextPageToken: String?
)

public data class UserDeviceToken(
  val userId: String,
  val token: String
)
