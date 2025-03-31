package tests

import models.Todo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import utils.IdGenerator.nextId
import kotlin.test.Test

class DeleteTodosTest : TodoApiBaseTest() {

    @Test
    fun `DELETE removes existing todo`() {
        val todo = Todo(id = nextId(), text = "To delete", completed = false)
        addTodo(todo)

        val response = service.deleteTodo(todo.id)
        assertThat("Expected 204 No Content after deletion", response.code, equalTo(204))

        val todos = service.getTodos()
        assertThat("Deleted todo should no longer be returned", todos.body?.map { it.id }, not(hasItem(todo.id)))
    }

    @Test
    fun `DELETE non-existent todo returns 404`() {
        val response = service.deleteTodo(nextId())
        assertThat("Expected 404 for deleting non-existent todo", response.code, equalTo(404))
    }

    @Test
    fun `DELETE without id should return 405`() {
        val response = service.deleteTodo()

        assertThat("Expected 405 Method Not Allowed when ID is missing", response.code, equalTo(405))
    }

    @Test
    fun `DELETE then re-create with same ID works`() {
        val id = nextId()
        val todo1 = Todo(id = id, text = "First version", completed = false)
        addTodo(todo1)

        val deleteResponse = service.deleteTodo(id)
        assertThat("Expected 204 on successful delete", deleteResponse.code, equalTo(204))

        val todo2 = Todo(id = id, text = "Recreated", completed = true)
        val postResponse = service.postTodo(todo2)
        assertThat("Expected 201 on re-creating todo with same ID", postResponse.code, equalTo(201))

        val fetched = service.getTodos().body?.find { it.id == id }
        assertThat("Re-created todo should be returned by GET", fetched, equalTo(todo2))
    }

    @Test
    fun `DELETE without auth should return 401`() {
        val todo = Todo(id = nextId(), text = "To delete", completed = false)
        addTodo(todo)

        val deleteResponse = service.deleteTodo(todo.id, null)
        assertThat("Expected 401 Unauthorized when no auth provided", deleteResponse.code, equalTo(401))
    }

// Additional test cases checklist:
//
// DELETE with invalid token → 403
// DELETE with invalid ID (e.g. string instead of number) → 400 or 404
// DELETE with Content-Type: application/xml → 415
// DELETE with request body (should be ignored or rejected)
// DELETE with path /todos// (double slash) → 404
}
