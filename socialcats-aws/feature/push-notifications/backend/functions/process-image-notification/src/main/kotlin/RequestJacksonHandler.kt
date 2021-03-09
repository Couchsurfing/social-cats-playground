package com.nicolasmilliard.socialcatsaws.pushnotifications.backend.functions

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.InputStream
import java.io.OutputStream

interface RequestJacksonHandler<I : Any, O : Any?> : RequestStreamHandler {
    val mapper: ObjectMapper
    val inputType: Class<I>

    fun handleRequest(input: I, context: Context): O?

    override fun handleRequest(inputStream: InputStream, outputStream: OutputStream, context: Context) {
        outputStream.use {
            handleRequest(inputStream.readJson(inputType, mapper), context).writeJsonNullable(outputStream, mapper)
        }

    }
}

private fun <T : Any> InputStream.readJson(clazz: Class<T>, mapper: ObjectMapper): T =
    mapper.readValue(this, clazz)


private fun Any?.writeJsonNullable(outputStream: OutputStream, mapper: ObjectMapper) {
    if (this != null)  mapper.writeValue(outputStream, this)
}