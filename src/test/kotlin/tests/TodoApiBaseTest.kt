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
import services.TodoService
import java.net.HttpURLConnection

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class TodoApiBaseTest {

    private val client = OkHttpClient()
    private var port: Int = 0
    protected lateinit var service: TodoService

    private val createdIds = mutableSetOf<Long>()

    @BeforeAll
    fun setup() {
        port = TestEnvironment.startDockerApp()
        val baseUrl = "${TestConfig.baseHttpUrl}:$port"
        waitForHealthCheck("$baseUrl/todos")
        service = TodoService(client, baseUrl)
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

    protected fun addTodo(id: Long, text: String, completed: Boolean = false): Todo {
        val todo = Todo(id, text, completed)
        service.postTodo(todo)
        createdIds.add(id)
        return todo
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
