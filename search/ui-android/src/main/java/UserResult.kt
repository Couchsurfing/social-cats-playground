package com.nicolasmilliard.socialcats.search

import com.nicolasmilliard.socialcats.model.User

internal data class UserResult(val query: String, val user: User)
