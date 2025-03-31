package tests

import models.Todo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Disabled
import utils.IdGenerator.nextId
import kotlin.test.Test

class UpdateTodosTest : TodoApiBaseTest() {

    @Test
    fun `PUT updates existing todo`() {
        val todo = Todo(id = nextId(), text = "Old todo", completed = false)
        addTodo(todo)

        val updated = todo.copy(text = "Updated todo", completed = true)
        val response = service.updateTodo(todo.id, updated)
        assertThat("Expected HTTP 200 OK on successful update", response.code, equalTo(200))

        val allTodos = service.getTodos()
        assertThat("Updated todo should be present in the list", allTodos.body, hasItem(equalTo(updated)))
    }

    @Test
    @Disabled
    fun `PUT with mismatched id in URL vs body returns 400`() {
        val original = Todo(id = nextId(), text = "Original", completed = false)
        addTodo(original)

        val mismatched = Todo(id = nextId(), text = "Wrong ID in body", true)

        val response = service.updateTodo(original.id, mismatched)
        assertThat("Expected HTTP 400 Bad Request due to ID mismatch", response.code, equalTo(400))
    }

    @Test
    fun `PUT with non-existent id returns 404`() {
        val fake = Todo(id = nextId(), text = "Fake todo", completed = true)
        val response = service.updateTodo(fake.id, fake)
        assertThat("Expected HTTP 404 Not Found for non-existent ID", response.code, equalTo(404))
    }

    @Test
    fun `PUT with invalid body returns 400`() {
        val original = Todo(id = nextId(), text = "Original", completed = false)
        addTodo(original)
        val raw = """{"text":"Missing fields"}"""
        val response = service.updateRaw(original.id, raw)
        assertThat("Expected HTTP 400 Bad Request for missing fields", response.code, equalTo(400))
        assertThat("Error message should mention missing field", response.rawBody, containsString("missing field"))
    }

    @Test
    fun `PUT with extra fields should ignore unexpected fields and return 200`() {
        val todo = Todo(id = nextId(), text = "Original", completed = false)
        val updatedText = "Updated_text"
        val unexpectedField = "unexpected_field"
        val anotherUnexpectedField = "another_one"
        addTodo(todo)

        val jsonWithExtra = """
        {
            "id": ${todo.id},
            "text": "$updatedText",
            "completed": true,
            "$unexpectedField": "I shouldn't be here",
            "$anotherUnexpectedField": 123
        }
    """.trimIndent()

        val response = service.updateRaw(todo.id, jsonWithExtra)
        assertThat("Expected HTTP 200 OK even with extra fields", response.code, equalTo(200))

        val updated = service.getTodos().body?.find { it.id == todo.id }
        assertThat("Updated text should match new value", updated?.text, equalTo(updatedText))
        assertThat("Updated completed flag should match new value", updated?.completed, equalTo(true))

        val rawJson = service.getTodos().rawBody.orEmpty()
        assertAll("Unexpected fields should not be present in response", {
            assertThat(rawJson.contains(unexpectedField), equalTo(false))
            assertThat(rawJson.contains(anotherUnexpectedField), equalTo(false))
        })
    }

// Additional test cases checklist:
//
// PUT with empty request body → 400
// PUT with invalid types (e.g. "completed": "yes", "id": "abc") → 400
// PUT with invalid Content-Type header (e.g. text/plain) → 415
// PUT with only 1 field (partial update) → 400 or 200
// PUT without authorization → 401
// PUT with invalid token → 403
}
