package tests

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import utils.IdGenerator.nextRange
import kotlin.test.Test

class GetTodosTest : TodoApiBaseTest() {

    companion object {
        const val INVALID_QUERY_MESSAGE = "Invalid query string"
    }

    @Test
    fun `GET returns all todos`() {
        nextRange(10).forEach { addTodo(it, "Todo $it") }

        val response = service.getTodos()
        assertThat("Expected 200 OK on GET todos", response.code, equalTo(200))
        assertThat("Should return exactly 10 todos", response.body?.size, equalTo(10))
    }

    @Test
    fun `pagination returns expected todos`() {
        val allIds = nextRange(10).toList()
        val offset = 5
        val limit = 3
        allIds.forEach { addTodo(it, "Todo $it", true) }

        val response = service.getTodos(offset, limit)
        assertThat("Expected 200 OK on paginated GET", response.code, equalTo(200))

        val expectedIds = allIds.drop(offset).take(limit)
        val actualIds = response.body?.map { it.id }
        assertThat("Returned todos should match expected pagination range", actualIds, equalTo(expectedIds))
    }

    @Test
    fun `invalid limit value returns 400`() {
        val limit = -1
        val response = service.getTodos(limit = limit)
        assertThat("Expected HTTP 400 Bad Request", response.code, equalTo(400))
        assertThat("Expected message is $INVALID_QUERY_MESSAGE", response.rawBody, equalTo(INVALID_QUERY_MESSAGE))
    }

    @Test
    fun `invalid offset value returns 400`() {
        val offset = -1
        val response = service.getTodos(offset = offset)
        assertThat("Expected HTTP 400 Bad Request", response.code, equalTo(400))
        assertThat("Expected message is $INVALID_QUERY_MESSAGE", response.rawBody, equalTo(INVALID_QUERY_MESSAGE))
    }

    @Test
    @Disabled("Enable test after fix ")
    fun `GET todos should return 403 when there's invalid token`() {
        val response = service.getTodos(credAuth = "bad:token")
        assertThat("Expected HTTP 403 Forbidden", response.code, equalTo(403))
    }

// Additional test cases checklist:
//
// GET with limit = 0 (edge case) -> empty list or 400
// GET with offset >= total count —> empty list
// GET with no auth -> 401
// GET when no todos exist —> empty list
}
