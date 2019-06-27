package com.nicolasmilliard.socialcats.search

import com.nicolasmilliard.socialcats.model.User

interface UserHandler {
    operator fun invoke(user: User)
}
