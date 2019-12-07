package app.kojima.jiko.boox.model.entities

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class Book(
    var userId: String = "",
    val isbn: String = "",
    val title: String ="",
    var userName: String? ="",
    var photoUrl: String? ="",
    val author: String = "",
    val created: Date = Date(),
    val itemUrl: String? = null,
    @SerializedName("largeImageUrl") val thumb: String =""
): Serializable

data class BookResponse(@SerializedName("Item") val book: Book)

data class BookResponses(@SerializedName("Items") val items: List<BookResponse>)

class BookResponseError: Error()