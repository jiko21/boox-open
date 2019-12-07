package app.kojima.jiko.boox.model.services.api

import app.kojima.jiko.boox.model.entities.BookResponses
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {
    @GET("/services/api/BooksBook/Search/20170404")
    fun getBooks(@Query("applicationId") applicationId: String = "楽天書籍APIのapplication ID", @Query("title") title: String): Observable<BookResponses>
}