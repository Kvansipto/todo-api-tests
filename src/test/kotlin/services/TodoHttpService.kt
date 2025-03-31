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
    baseUrl: String,
    private val baseAuth: String?
) {
    companion object {
        const val BASE_PATH = "/todos"
    }

    private val todoHttpServiceUrl: String = "$baseUrl$BASE_PATH"

    fun getTodos(
        offset: Int? = null,
        limit: Int? = null,
        credAuth: String? = baseAuth
    ): ApiResponse<List<Todo>> {
        val httpUrl = todoHttpServiceUrl.toHttpUrlOrNull()!!
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
        return postRaw(todo.toJson(), credAuth)
    }

    fun postRaw(
        json: String,
        credAuth: String? = baseAuth
    ): ApiResponse<Unit> {
        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(todoHttpServiceUrl)
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
        val url = if (id != null) "$todoHttpServiceUrl/$id" else "$todoHttpServiceUrl/"
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
        return updateRaw(id, todo.toJson(), credAuth)
    }

    fun updateRaw(
        id: Long,
        json: String,
        credAuth: String? = baseAuth
    ): ApiResponse<Unit> {
        val body = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$todoHttpServiceUrl/$id")
            .withAuth(credAuth)
            .put(body)
            .build()
        val response = client.newCall(request).execute()
        return ApiResponse.from(response)
    }
}