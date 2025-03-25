package tests

import okhttp3.Request
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class GetTodosTest : TodoApiBaseTest() {

    @Test
    fun `GET todos should return 200`() {
        val url = "http://localhost:$port/todos"

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).execute().use { response ->
            assertEquals(200, response.code, "Expected HTTP 200 OK")
        }
    }
}
