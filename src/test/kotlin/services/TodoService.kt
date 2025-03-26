package services

import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class TodoService(
    private val client: OkHttpClient = OkHttpClient(),
    private val baseUrl: String
) {
    fun getTodos(offset: Int? = null, limit: Int? = null): Response {
        val httpUrl = "$baseUrl/todos".toHttpUrlOrNull()!!
            .newBuilder()
            .apply {
                offset?.let { addQueryParameter("offset", it.toString()) }
                limit?.let { addQueryParameter("limit", it.toString()) }
            }
            .build()

        val request = Request.Builder()
            .url(httpUrl)
            .get()
            .build()

        return client.newCall(request).execute()
    }
}