package wrappers

import kotlinx.serialization.json.Json

data class WsMessage<T>(
    val rawMessage: String,
    val body: T?
) {
    companion object {
        inline fun <reified T> from(rawMessage: String): WsMessage<T> {
            val parsed = rawMessage.takeIf { it.isNotBlank() }?.let {
                try {
                    Json.decodeFromString<T>(it)
                } catch (_: Exception) {
                    null
                }
            }
            return WsMessage(
                rawMessage = rawMessage,
                body = parsed
            )
        }
    }
}
