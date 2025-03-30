package services

import models.Todo
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import wrappers.ApiResponse
import utils.withAuth

class TodoHttpService(
    private val client: OkHttpClient = OkHttpClient(),
    private val baseUrl: String,
    private val baseAuth: String?
) {
    val basePath = "/todos"

    fun getTodos(
        offset: Int? = null,
        limit: Int? = null,
        credAuth: String? = baseAuth
    ): ApiResponse<List<Todo>> {
        val httpUrl = "$baseUrl$basePath".toHttpUrlOrNull()!!
            .newBuilder()
            .apply {
                offset?.let { addQueryParameter("offset", it.toString()) }
                limit?.let { addQueryParameter("limit", it.toString()) }
            }
            .build()

        val request = Request.Builder()
            .url(httpUrl)
            .withAuth(credAuth)
            .get()
            .build()

        val response = client.newCall(request).execute()

        return ApiResponse.from(response)
    }

    fun postTodo(
        todo: Todo,
        credAuth: String? = baseAuth
    ): ApiResponse<Unit> {
        val json = todo.toJson()
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl$basePath")
            .withAuth(credAuth)
            .post(requestBody)
            .build()
        val response = client.newCall(request).execute()

        return ApiResponse.from(response)
    }

    fun postRaw(
        json: String,
        credAuth: String? = baseAuth
    ): ApiResponse<Unit> {
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl$basePath")
            .withAuth(credAuth)
            .post(requestBody)
            .build()
        val response = client.newCall(request).execute()

        return ApiResponse.from(response)
    }

    fun deleteTodo(
        id: Long? = null,
        credAuth: String? = baseAuth
    ): ApiResponse<Unit> {
        val url = if (id != null) "$baseUrl$basePath/$id" else "$baseUrl$basePath/"
        val request = Request.Builder()
            .url(url)
            .withAuth(credAuth)
            .delete()
            .build()

        val response = client.newCall(request).execute()
        return ApiResponse.from(response)
    }

    fun updateTodo(
        id: Long,
        todo: Todo,
        credAuth: String? = baseAuth
    ): ApiResponse<Unit> {
        val json = todo.toJson()
        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl$basePath/$id")
            .withAuth(credAuth)
            .put(body)
            .build()
        val response = client.newCall(request).execute()
        return ApiResponse.from(response)
    }

    fun updateRaw(
        id: Long,
        json: String,
        credAuth: String? = baseAuth
    ): ApiResponse<Unit> {
        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$baseUrl$basePath/$id")
            .withAuth(credAuth)
            .put(body)
            .build()
        val response = client.newCall(request).execute()
        return ApiResponse.from(response)
    }
}