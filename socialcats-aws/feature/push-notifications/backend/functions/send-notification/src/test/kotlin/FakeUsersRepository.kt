package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.nicolasmilliard.socialcatsaws.profile.model.Device
import com.nicolasmilliard.socialcatsaws.profile.model.Image
import com.nicolasmilliard.socialcatsaws.profile.model.User
import com.nicolasmilliard.socialcatsaws.profile.model.UserWithImages
import com.nicolasmilliard.socialcatsaws.profile.repository.InsertResult
import com.nicolasmilliard.socialcatsaws.profile.repository.TokensResult
import com.nicolasmilliard.socialcatsaws.profile.repository.UserDeviceToken
import com.nicolasmilliard.socialcatsaws.profile.repository.UsersRepository

class FakeUsersRepository : UsersRepository{
    override fun getUserAndImages(userId: String, newestImagesCount: Int): UserWithImages {
        TODO("Not yet implemented")
    }

    override fun updateUser(user: User): User {
        TODO("Not yet implemented")
    }

    override fun getUserById(id: String): User {
        TODO("Not yet implemented")
    }

    override fun insertImage(image: Image): InsertResult {
        TODO("Not yet implemented")
    }

    override fun countImages(userId: String): Int {
        TODO("Not yet implemented")
    }

    override fun insertDevice(device: Device) {
        TODO("Not yet implemented")
    }

    var deletedDevices: MutableList<List<UserDeviceToken>> = mutableListOf()

    override fun deleteDevices(devices: List<UserDeviceToken>) {
        deletedDevices.add(devices)
    }

    override fun getDeviceTokens(userId: String, limit: Int, pageToken: String?): TokensResult {
        TODO("Not yet implemented")
    }
}