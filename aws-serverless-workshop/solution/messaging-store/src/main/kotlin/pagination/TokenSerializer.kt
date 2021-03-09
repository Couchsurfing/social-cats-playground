package com.nicolasmilliard.serverlessworkshop.messaging.pagination

interface TokenSerializer<T> {
    /**
     * Deserialize the token into type T.
     *
     * @param token token in String.
     * @return deserialized token.
     * @throws InvalidTokenException throws when the token in String is invalid.
     */
    fun deserialize(token: String): T

    /**
     * Serialize the token into a String.
     *
     * @param token the token to be serialized.
     * @return serialized token in String.
     */
    fun serialize(token: T): String
}
