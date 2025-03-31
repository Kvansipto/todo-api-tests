package tests

import models.Todo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import utils.IdGenerator.nextId
import kotlin.test.Test

class PostTodosTest : TodoApiBaseTest() {

    @Test
    fun `POST creates todo and returns 201`() {
        val todo = Todo(id = nextId(), text = "New todo", completed = false)
        val response = addTodo(todo)

        assertThat("Expected HTTP 201 Created", response.code, equalTo(201))

        val allTodos = service.getTodos()
        assertThat("Created todo should be present in the list", allTodos.body, hasItem(equalTo(todo)))
    }

    @Test
    fun `POST duplicate id returns 400`() {
        val todo = Todo(id = nextId(), text = "Duplicate", completed = false)
        val response = addTodo(todo)
        assertThat("Expected HTTP 201 Created on first insert", response.code, equalTo(201))

        val duplicate = service.postTodo(todo)
        assertThat("Expected HTTP 400 Bad Request on duplicate ID", duplicate.code, equalTo(400))
    }

    @Test
    fun `POST with edge id values works`() {
        val min = Todo(id = 0, text = "Min ID", completed = false)
        val max = Todo(id = Long.MAX_VALUE, text = "Max ID", completed = true)

        val resMin = addTodo(min)
        val resMax = addTodo(max)

        assertThat("Expected HTTP 201 Created for min ID", resMin.code, equalTo(201))
        assertThat("Expected HTTP 201 Created for max ID", resMax.code, equalTo(201))
    }

    @Test
    fun `POST with missing fields returns 400`() {
        val body = """{"text":"Missing fields"}"""
        val response = service.postRaw(body)
        assertThat("Expected HTTP 400 Bad Request for missing fields", response.code, equalTo(400))
        assertThat("Error message should mention missing field", response.rawBody, containsString("missing field"))
    }

    @Test
    fun `POST with invalid JSON returns 400`() {
        val brokenJson = "{"
        val response = service.postRaw(brokenJson)
        assertThat("Expected HTTP 400 Bad Request for invalid JSON", response.code, equalTo(400))
        assertThat("Error message should indicate JSON parsing error", response.rawBody, containsString("EOF while parsing an object"))
    }

// Additional test cases checklist:
//
// POST with no auth user or invalid token -> 401/403
// POST with id with negative sign -> 400
// POST with empty `text` field -> 400
// POST with extremely long `text` -> 400
// POST with invalid content-type header -> 415
// POST with empty request body -> 400
// POST with extra unexpected fields in body -> 400
}
