package tests

import models.Todo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Disabled
import kotlin.test.Test

open class UpdateTodosTest : TodoApiBaseTest() {

    @Test
    fun `PUT updates existing todo`() {
        val todo = Todo(id = 1L, text = "Old todo", completed = false)
        addTodo(todo)

        val updated = todo.copy(text = "Updated todo", completed = true)
        val response = service.updateTodo(todo.id, updated)
        assertThat(response.code, equalTo(200))

        val allTodos = service.getTodos()
        assertThat(allTodos.body, hasItem(equalTo(updated)))
    }

    @Test
    @Disabled
    fun `PUT with mismatched id in URL vs body returns 400`() {
        val original = Todo(id = 2L, text = "Original", completed = false)
        addTodo(original)

        val mismatched = Todo(id = 3L, text = "Wrong ID in body", true)

        val response = service.updateTodo(original.id, mismatched)
        assertThat(response.code, equalTo(400))
    }

    @Test
    fun `PUT with non-existent id returns 404`() {
        val fake = Todo(id = 99999L, text = "Fake todo", completed = true)
        val response = service.updateTodo(fake.id, fake)
        assertThat(response.code, equalTo(404))
    }

    @Test
    fun `PUT with invalid body returns 400`() {
        val raw = """{"text":"Missing fields"}"""
        val response = service.updateRaw(4L, raw)
        assertThat(response.code, equalTo(400))
        assertThat(response.rawBody, containsString("missing field"))
    }

    @Test
    fun `PUT with extra fields should ignore unexpected fields and return 200`() {
        val todo = Todo(id = 5L, text = "Original", completed = false)
        addTodo(todo)

        val jsonWithExtra = """
        {
            "id": ${todo.id},
            "text": "Updated text",
            "completed": true,
            "unexpected_field": "I shouldn't be here",
            "another_one": 123
        }
    """.trimIndent()

        val response = service.updateRaw(todo.id, jsonWithExtra)
        assertThat(response.code, equalTo(200))

        val updated = service.getTodos().body?.find { it.id == todo.id }
        assertThat(updated?.text, equalTo("Updated text"))
        assertThat(updated?.completed, equalTo(true))

        val rawJson = service.getTodos().rawBody.orEmpty()
        assertThat(rawJson.contains("unexpected_field"), equalTo(false))
        assertThat(rawJson.contains("another_one"), equalTo(false))
    }

// Additional test cases checklist for PUT /todos:
//
// PUT with empty request body → 400
// PUT with invalid types (e.g. "completed": "yes", "id": "abc") → 400
// PUT with invalid Content-Type header (e.g. text/plain) → 415
// PUT with only 1 field (partial update) → 400 or 200
// PUT without authorization → 401
// PUT with invalid token → 403
}
