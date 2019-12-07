package app.kojima.jiko.boox.model.entities

import java.io.Serializable

data class User(
    val uid: String = "",
    val name: String = "",
    val icon: String = "",
    val likes: Long = 0
): Serializable