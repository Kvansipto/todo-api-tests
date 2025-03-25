package tests

import infra.TestEnvironment
import okhttp3.OkHttpClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class TodoApiBaseTest {

    protected val client = OkHttpClient()
    protected var port: Int = 0

    @BeforeAll
    fun setup() {
        port = TestEnvironment.startDockerApp()
    }

    @AfterAll
    fun cleanup() {
        TestEnvironment.stop()
    }
}
