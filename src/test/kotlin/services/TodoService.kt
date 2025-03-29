package services

import infra.ApiResponse
import models.Todo
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class TodoService(
    private val client: OkHttpClient = OkHttpClient(),
    private val baseUrl: String
) {
    private val basePath = "/todos"

    fun getTodos(offset: Int? = null, limit: Int? = null): ApiResponse<List<Todo>> {
        val httpUrl = "$baseUrl$basePath".toHttpUrlOrNull()!!
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
        val response = client.newCall(request).execute()

        return ApiResponse.from(response)
    }

    fun postTodo(todo: Todo): ApiResponse<Unit> {
        val json = todo.toJson()
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl$basePath")
            .post(requestBody)
            .build()
        val response = client.newCall(request).execute()

        return ApiResponse.from(response)
    }
}