package tests

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.empty
import org.hamcrest.Matchers.equalTo
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
    fun `GET with offset greater than total count should return empty list`() {
        val todosCount = 10
        nextRange(todosCount).forEach { addTodo(it, "Todo $it") }

        val response = service.getTodos(offset = todosCount)
        assertThat("Expected 200 OK on GET todos", response.code, equalTo(200))
        assertThat("Expected empty list", response.body, empty())
    }

// Additional test cases checklist:
//
// GET with limit = 0 (edge case) -> empty list or 400
// GET with no auth -> 401
// GET with invalid auth -> 403
// GET when no todos exist —> empty list
}
