package com.nicolasmilliard.socialcats.search.repository

inline fun <T> measureTimeMillis(
    loggingFunction: (Long) -> Unit,
    function: () -> T
): T {

    val startTime = System.currentTimeMillis()
    val result: T = function.invoke()
    loggingFunction.invoke(System.currentTimeMillis() - startTime)

    return result
}
