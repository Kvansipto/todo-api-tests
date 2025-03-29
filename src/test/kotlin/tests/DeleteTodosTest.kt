package tests

import models.Todo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Disabled
import kotlin.test.Test

class DeleteTodosTest : TodoApiBaseTest() {

    @Test
    fun `DELETE removes existing todo`() {
        val todo = Todo(id = 1L, text = "To delete", completed = false)
        addTodo(todo)

        val response = service.deleteTodo(todo.id)
        assertThat(response.code, equalTo(204))

        val todos = service.getTodos()
        assertThat(todos.body?.map { it.id }, not(hasItem(todo.id)))
    }

    @Test
    fun `DELETE non-existent todo returns 404`() {
        val response = service.deleteTodo(999999L)
        assertThat(response.code, equalTo(404))
    }

    @Test
    fun `DELETE without id should return 405`() {
        val response = service.deleteTodo()

        assertThat(response.code, equalTo(405))
    }

    @Test
    fun `DELETE then re-create with same ID works`() {
        val id = 9100L
        val todo1 = Todo(id = id, text = "First version", completed = false)
        addTodo(todo1)

        val deleteResponse = service.deleteTodo(id)
        assertThat(deleteResponse.code, equalTo(204))

        val todo2 = Todo(id = id, text = "Recreated", completed = true)
        val postResponse = service.postTodo(todo2)
        assertThat(postResponse.code, equalTo(201))

        val fetched = service.getTodos().body?.find { it.id == id }
        assertThat(fetched, equalTo(todo2))
    }

    @Test
    @Disabled
    fun `DELETE works even after PUT with mismatched ID in body`() {
        val id = 3001L
        val original = Todo(id = id, text = "Original", completed = false)
        addTodo(original)

        val mismatched = original.copy(id = 9999L, text = "Hacked", completed = true)
        val putResponse = service.updateTodo(id, mismatched)

        assertThat(putResponse.code, equalTo(400))

        val deleteResponse = service.deleteTodo(id)
        assertThat(deleteResponse.code, equalTo(204))

        val remaining = service.getTodos().body?.map { it.id }
        assertThat(remaining, not(hasItem(id)))
    }

// Additional test cases checklist:
//
// DELETE without authorization → 401
// DELETE with invalid token → 403
// DELETE with invalid ID (e.g. string instead of number) → 400 or 404
// DELETE with Content-Type: application/xml → 415
// DELETE with request body (should be ignored or rejected)
// DELETE with path /todos// (double slash) → 404
}
