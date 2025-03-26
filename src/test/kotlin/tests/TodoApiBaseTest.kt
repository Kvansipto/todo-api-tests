package tests

import infra.TestConfig
import infra.TestEnvironment
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import services.TodoService

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class TodoApiBaseTest {

    private val client = OkHttpClient()
    private var port: Int = 0
    protected lateinit var service: TodoService

    @BeforeAll
    fun setup() {
        port = TestEnvironment.startDockerApp()
        val baseUrl = "${TestConfig.baseHttpUrl}:$port"
        service = TodoService(client, baseUrl)
    }

    @AfterAll
    fun cleanup() {
        TestEnvironment.stop()
    }
}
