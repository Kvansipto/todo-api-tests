package infra

import kotlinx.serialization.json.Json
import okhttp3.Response

data class ApiResponse<T>(
    val code: Int,
    val raw: Response,
    val rawBody: String?,
    val body: T?
) {
    companion object {
        inline fun <reified T> from(response: Response): ApiResponse<T> {
            val rawBody = response.body?.string()
            val parsedBody = rawBody?.takeIf {
                it.isNotBlank()
            }?.let {
                try {
                    Json.decodeFromString<T>(it)
                } catch (e: Exception) {
                    null
                }
            }
            return ApiResponse(
                code = response.code,
                raw = response,
                rawBody = rawBody,
                body = parsedBody
            )
        }
    }
}
