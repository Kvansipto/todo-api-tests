package tests

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import kotlin.test.Test

open class GetTodosTest : TodoApiBaseTest() {

    @Test
    fun `GET returns all todos`() {
        (1L..10L).forEach { addTodo(it, "Todo $it") }

        val response = service.getTodos()
        assertThat(response.code, equalTo(200))
        assertThat(response.body?.size, equalTo(10))
    }

    @Test
    fun `pagination returns expected todos`() {
        val allIds = (1L..10L).toList()
        val offset = 5
        val limit = 3
        allIds.forEach { addTodo(it, "Todo $it", true) }

        val response = service.getTodos(offset, limit)
        assertThat(response.code, equalTo(200))

        val expectedIds = allIds.drop(offset).take(limit)
        val actualIds = response.body?.map { it.id }
        assertThat(actualIds, equalTo(expectedIds))
    }

    @Test
    fun `invalid limit value returns 400`() {
        val expectedMessage = "Invalid query string"
        val limit = -1
        val response = service.getTodos(limit = limit)
        assertThat("Expected HTTP 400 Bad Request", response.code, equalTo(400))
        assertThat("Expected message is $expectedMessage", response.rawBody, equalTo(expectedMessage))
    }

    @Test
    fun `invalid offset value returns 400`() {
        val expectedMessage = "Invalid query string"
        val offset = -1
        val response = service.getTodos(offset = offset)
        assertThat("Expected HTTP 400 Bad Request", response.code, equalTo(400))
        assertThat("Expected message is $expectedMessage", response.rawBody, equalTo(expectedMessage))
    }

    @Test
    @Disabled
    fun `GET todos should return 403 when there's invalid token`() {
        val response = service.getTodos(credAuth = "bad:token")
        assertThat("Expected HTTP 403 Forbidden", response.code, equalTo(403))
    }

// Additional test cases checklist:
//
// GET with limit = 0 (edge case)
// GET with offset >= total count — should return empty list
// GET with no auth -> 401
// GET when no todos exist — should return empty array
}
