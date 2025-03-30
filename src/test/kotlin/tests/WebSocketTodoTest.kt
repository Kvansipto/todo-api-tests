package tests

import models.Todo
import models.TodoMessage
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.hasItems
import org.hamcrest.MatcherAssert.assertThat
import kotlin.test.Test

class WebSocketTodoTest : TodoApiBaseTest() {

    @Test
    fun `WS notifies about new todo`() {
        val todo = Todo(1L, "new WS todo", false)
        val msg = wsService.listenOnceAfter<TodoMessage> { addTodo(todo) }

        assertThat(msg.body?.type, equalTo("new_todo"))
        assertThat(msg.body?.data, equalTo(todo))
    }

    @Test
    fun `WS receives exactly N messages in right order`() {
        val todos = (10L..14L).map { Todo(it, "todo $it", false) }

        val messages = wsService.listenManyAfter<TodoMessage>(count = todos.size) {
            todos.forEach(::addTodo)
        }

        assertThat(messages.size, equalTo(todos.size))
        assertThat(messages.mapNotNull { it.body?.data?.id }, equalTo(todos.map { it.id }))
    }

    @Test
    fun `WebSocket only notifies on creation`() {

        val todo1 = Todo(1L, "first WS todo", false)
        val todo2 = Todo(1L, "second WS todo", true)

        val messages = wsService.listenManyAfter<TodoMessage>(count = 2) {
            addTodo(todo1)

            val updated = todo1.copy(text = "Updated", completed = true)
            service.updateTodo(updated.id, updated)

            service.deleteTodo(updated.id)

            addTodo(todo2)
        }

        assertThat("Should receive only 2 WS messages", messages.size, equalTo(2))
        assertThat(
            "Messages should have 2 created todos",
            messages.map { it.body?.data },
            hasItems(todo1, todo2)
        )
    }

}