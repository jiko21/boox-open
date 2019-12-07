package app.kojima.jiko.boox.model.entities

import java.util.*

data class Comment(
    var bookId: String = "",
    var userId: String = "",
    var userName: String? = "",
    var usreIcon: String? = "",
    val content: String = "",
    var like: Long = 0L,
    val created: Date = Date()
)