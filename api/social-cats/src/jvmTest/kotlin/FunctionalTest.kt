package com.nicolasmilliard.socialcats.search

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("functional")
class FunctionalTest {
    @Test
    fun searchTest() = runBlocking {
        println("Search test")

        val searchService = SocialCatsApiModule.searchService(OkHttpClient())
        val searchUsers = searchService.searchUsers(null, null)
        println("Search user result: $searchUsers")
        assertThat(searchUsers.users).isNotEmpty()
    }
}
