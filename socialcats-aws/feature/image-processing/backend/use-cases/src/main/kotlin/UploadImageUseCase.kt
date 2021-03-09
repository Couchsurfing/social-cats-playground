package com.nicolasmilliard.socialcatsaws.profile

import app.cash.exhaustive.Exhaustive
import com.nicolasmilliard.cloudmetric.CloudMetrics
import com.nicolasmilliard.cloudmetric.Unit
import com.nicolasmilliard.repository.imageobjectstore.ImageObjectStore
import com.nicolasmilliard.repository.imageobjectstore.ImageStoreKey
import com.nicolasmilliard.socialcatsaws.profile.model.Avatar
import com.nicolasmilliard.socialcatsaws.profile.model.CreateSignedUrl
import com.nicolasmilliard.socialcatsaws.profile.model.Image
import com.nicolasmilliard.socialcatsaws.profile.repository.InsertResult
import com.nicolasmilliard.socialcatsaws.profile.repository.UsersRepository
import kotlinx.datetime.Instant
import mu.KotlinLogging
import mu.withLoggingContext
import javax.inject.Inject

private val MEGABYTE = 1024L * 1024L
private val logger = KotlinLogging.logger {}

private const val MAX_STORED_IMAGES = 10
public class UploadImageUseCase @Inject constructor(
  private val usersRepository: UsersRepository,
  private val imageObjectStore: ImageObjectStore,
  private val cloudMetrics: CloudMetrics
) {
  public fun createSignedUrl(userId: String): CreateSignedUrl {
    val imageCount = usersRepository.countImages(userId)
    if (imageCount >= MAX_STORED_IMAGES) {
      logger.warn { "event=too_many_images_stored" }
      cloudMetrics.putMetric("TooManyImageStoredCount", 1.0, Unit.COUNT)
      return CreateSignedUrl.MaxStoredImagesReached
    }
    val key = ImageStoreKey.create(userId)
    val preSignedUrl = imageObjectStore.createPreSignedUrl(key)
    logger.info { "event=image_upload_url_created" }
    cloudMetrics.putMetric("ImageUploadUrlCreatedCount", 1.0, Unit.COUNT)
    return CreateSignedUrl.CreateSignedUrlData(preSignedUrl.url, preSignedUrl.headers)
  }

  public fun onNewStoredImage(storeKey: String, size: Long, eventTime: String) {
    val imageStoreKey = ImageStoreKey(storeKey)
    val userId = imageStoreKey.userId
    withLoggingContext("UserId" to userId) {
      if (size / MEGABYTE > 6) {
        logger.warn("event=image_stored_too_big")
        imageObjectStore.deleteImage(imageStoreKey)
        cloudMetrics.putMetric("ImageStoredTooBigCount", 1.0, Unit.COUNT)
      } else {
        val imageCount = usersRepository.countImages(userId)
        if (imageCount + 1 >= MAX_STORED_IMAGES) {
          logger.warn { "event=too_many_images_stored" }
          cloudMetrics.putMetric("TooManyImageStoredCount", 1.0, Unit.COUNT)
          imageObjectStore.deleteImage(imageStoreKey)
        }
        val image = Image(id = storeKey, userId = userId, Instant.parse(eventTime))

        val result = usersRepository.insertImage(image)

        @Exhaustive
        when (result) {
          is InsertResult.Added -> {
            logger.info("event=repository_new_image")
            cloudMetrics.putMetric("ImageRepositoryCreatedCount", 1.0, Unit.COUNT)
          }
          is InsertResult.AlreadyExist -> {
            logger.info("event=repository_new_image_already_added")
            cloudMetrics.putMetric("ImageRepositoryAlreadyAddedCount", 1.0, Unit.COUNT)
          }
        }
        updateAvatar(image)
      }
    }
  }

  private fun updateAvatar(image: Image) {
    val user = usersRepository.getUserById(image.userId)
    if (user.avatar?.imageId == image.id) {
      logger.warn { IllegalStateException("Trying ot update and url to the same one") }
      return
    }
    usersRepository.updateUser(user.copy(avatar = Avatar(image.id)))
    logger.info { "event=avatar_updated" }
  }
}
