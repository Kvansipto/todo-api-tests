package models

import kotlinx.serialization.Serializable

@Serializable
data class TodoMessage(
    val type: String,
    val data: Todo
)
