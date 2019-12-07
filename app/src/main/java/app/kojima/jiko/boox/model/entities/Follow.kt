package app.kojima.jiko.boox.model.entities

data class Follow(
    val followList: HashMap<String, User> = hashMapOf()
)
