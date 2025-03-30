package tests

import infra.TestConfig
import infra.TestEnvironment
import models.Todo
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import services.TodoHttpService
import services.TodoWebSocketService
import wrappers.ApiResponse
import java.net.HttpURLConnection

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class TodoApiBaseTest {

    private val client = OkHttpClient()
    private var port: Int = 0
    protected lateinit var service: TodoHttpService
    protected lateinit var wsService: TodoWebSocketService

    private val createdIds = mutableSetOf<Long>()

    @BeforeAll
    fun setup() {
        port = TestEnvironment.startDockerApp()
        val baseHttpUrl = "${TestConfig.baseHttpUrl}:$port"
        val baseWs = baseHttpUrl.replace("http", "ws")
        service = TodoHttpService(client, baseHttpUrl, TestConfig.baseAuth)
        waitForHealthCheck("$baseHttpUrl${service.basePath}")
        wsService = TodoWebSocketService("$baseWs/ws", TestConfig.baseAuth, client)
    }

    @AfterAll
    fun cleanup() {
        TestEnvironment.stop()
    }

    @AfterEach
    fun cleanupTodos() {
        createdIds.forEach { id ->
            service.deleteTodo(id)
        }
        createdIds.clear()
    }

    protected fun addTodo(id: Long, text: String, completed: Boolean = false): ApiResponse<Unit> {
        val todo = Todo(id, text, completed)
        createdIds.add(id)
        return service.postTodo(todo)
    }

    protected fun addTodo(todo: Todo): ApiResponse<Unit> {
        return addTodo(todo.id, todo.text, todo.completed)
    }

    private fun waitForHealthCheck(url: String, retries: Int = 20, delayMillis: Long = 300L) {
        repeat(retries) {
            try {
                val conn = url.toHttpUrl().toUrl().openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                if (conn.responseCode == 200) return
            } catch (_: Exception) {
            }
            Thread.sleep(delayMillis)
        }
        error("Health check failed for GET /todos after ${retries * delayMillis}ms")
    }
}
