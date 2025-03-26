package tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.TestInstance
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class GetTodosTest : TodoApiBaseTest() {

    @Test
    fun `GET todos should return 200`() {
        val response = service.getTodos()

        assertEquals(200, response.code, "Expected HTTP 200 OK")
    }
}
