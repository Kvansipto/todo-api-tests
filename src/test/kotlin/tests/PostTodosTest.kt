package tests

import models.Todo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import kotlin.test.Test

open class PostTodosTest : TodoApiBaseTest() {

    @Test
    fun `POST creates todo and returns 201`() {
        val todo = Todo(id = 1001L, text = "New todo", completed = false)
        val response = addTodo(todo)

        assertThat(response.code, equalTo(201))

        val allTodos = service.getTodos()
        assertThat(allTodos.body, hasItem(equalTo(todo)))
    }

    @Test
    fun `POST duplicate id returns 400`() {
        val todo = Todo(id = 2002L, text = "Duplicate", completed = false)
        val response = addTodo(todo)
        assertThat(response.code, equalTo(201))

        val duplicate = service.postTodo(todo)
        assertThat(duplicate.code, equalTo(400))
    }

    @Test
    fun `POST with edge id values works`() {
        val min = Todo(id = 0, text = "Min ID", completed = false)
        val max = Todo(id = Long.MAX_VALUE, text = "Max ID", completed = true)

        val resMin = addTodo(min)
        val resMax = addTodo(max)

        assertThat(resMin.code, equalTo(201))
        assertThat(resMax.code, equalTo(201))
    }

    @Test
    fun `POST with missing fields returns 400`() {
        val body = """{"text":"Missing fields"}"""
        val response = service.postRaw(body)
        assertThat(response.code, equalTo(400))
        assertThat(response.rawBody, containsString("missing field"))
    }

    @Test
    fun `POST with invalid JSON returns 400`() {
        val brokenJson = "{"
        val response = service.postRaw(brokenJson)
        assertThat(response.code, equalTo(400))
        assertThat(response.rawBody, containsString("EOF while parsing an object"))
    }

// Additional test cases checklist:
//
// POST with no auth user or invalid token
// POST with empty `text` field
// POST with extremely long `text`
// POST with invalid content-type header
// POST with empty request body
// POST with extra unexpected fields in body
}
