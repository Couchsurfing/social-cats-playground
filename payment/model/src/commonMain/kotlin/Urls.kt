package com.nicolasmilliard.socialcats.payment

const val BASE_URL = "https://social-cats-payment.web.app/membership/subscription"

class Urls(private val domain: String = BASE_URL) {

    val paymentCancel = "$domain/cancel"

    val paymentSuccess ="$domain/success?session_id={CHECKOUT_SESSION_ID}"

    fun buildClientCheckoutUrl(stripePublicKey: String, sessionId: String): String {
        val encodedKey = urlEncode(stripePublicKey)
        val encodedSessionId = urlEncode(sessionId)
        return "$domain/checkout?key=$encodedKey&sessionId=$encodedSessionId"
    }
}

expect fun urlEncode(value: String): String
