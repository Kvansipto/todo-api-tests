package utils

import okhttp3.Request

fun Request.Builder.withBasicAuth(credentials: String): Request.Builder {
    val encoded = java.util.Base64.getEncoder().encodeToString(credentials.toByteArray())
    return header("Authorization", "Basic $encoded")
}