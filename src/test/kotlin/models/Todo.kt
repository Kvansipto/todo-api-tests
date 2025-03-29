package models

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Todo(
    val id: Long,
    val text: String,
    val completed: Boolean
) {
    fun toJson(): String = Json.encodeToString(serializer(), this)
}
