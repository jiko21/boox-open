package app.kojima.jiko.boox.model.entities

data class Like(
    var commentId: String = "",
    var bookId: String = "",
    var userOfComment: String = "",
    var userId: String = ""
)
