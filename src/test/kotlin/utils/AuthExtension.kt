package utils

import okhttp3.Request

fun Request.Builder.withAuth(credentials: String?): Request.Builder {
    return credentials?.let {
        val encoded = java.util.Base64.getEncoder().encodeToString(credentials.toByteArray())
        addHeader("Authorization", "Basic $encoded") } ?: return this
}
