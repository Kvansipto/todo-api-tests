package services

import utils.ApiResponse
import utils.withBasicAuth
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

    fun getTodos(
        offset: Int? = null,
        limit: Int? = null,
        credAuth: String? = "admin:admin"
    ): ApiResponse<List<Todo>> {
        val httpUrl = "$baseUrl$basePath".toHttpUrlOrNull()!!
            .newBuilder()
            .apply {
                offset?.let { addQueryParameter("offset", it.toString()) }
                limit?.let { addQueryParameter("limit", it.toString()) }
            }
            .build()

        val request = buildRequestWithAuth(
            Request.Builder()
                .url(httpUrl)
                .get(), credAuth
        )
        val response = client.newCall(request).execute()

        return ApiResponse.from(response)
    }

    fun postTodo(
        todo: Todo,
        credAuth: String? = "admin:admin"
    ): ApiResponse<Unit> {
        val json = todo.toJson()
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = buildRequestWithAuth(
            Request.Builder()
                .url("$baseUrl$basePath")
                .post(requestBody), credAuth
        )
        val response = client.newCall(request).execute()

        return ApiResponse.from(response)
    }

    fun postRaw(
        json: String,
        credAuth: String? = "admin:admin"
    ): ApiResponse<Unit> {
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = buildRequestWithAuth(
            Request.Builder()
                .url("$baseUrl$basePath")
                .post(requestBody), credAuth
        )
        val response = client.newCall(request).execute()

        return ApiResponse.from(response)
    }

    fun deleteTodo(
        id: Long,
        credAuth: String? = "admin:admin"
    ): ApiResponse<Unit> {
        val request = buildRequestWithAuth(
            Request.Builder().url("$baseUrl$basePath/$id").delete(),
            credAuth
        )
        val response = client.newCall(request).execute()
        return ApiResponse.from(response)
    }

    fun updateTodo(
        id: Long,
        todo: Todo,
        credAuth: String? = "admin:admin"
    ): ApiResponse<Unit> {
        val json = todo.toJson()
        val body = json.toRequestBody("application/json".toMediaType())

        val request = buildRequestWithAuth(
            Request.Builder().url("$baseUrl$basePath/$id").put(body),
            credAuth
        )
        val response = client.newCall(request).execute()
        return ApiResponse.from(response)
    }

    fun updateRaw(
        id: Long,
        json: String,
        credAuth: String? = "admin:admin"
    ): ApiResponse<Unit> {
        val body = json.toRequestBody("application/json".toMediaType())

        val request = buildRequestWithAuth(
            Request.Builder().url("$baseUrl$basePath/$id").put(body),
            credAuth
        )
        val response = client.newCall(request).execute()
        return ApiResponse.from(response)
    }

    private fun buildRequestWithAuth(builder: Request.Builder, credAuth: String?): Request {
        return credAuth?.let { builder.withBasicAuth(credAuth).build() } ?: builder.build()
    }
}