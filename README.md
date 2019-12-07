# Boox!
公開用Ripository
(こちらのRipositoryは各種API keyを入れておりません。)

## 注意
- Firebase
Firebase AuthとCloud Firestoreが必要です。(Google-Serivce.json)
- Twitter, Facebook
それぞれ、ソーシャルログイン用のAPI keyを用意してください。
```strings.xml
<string name="facebook_app_id">facebook_app_id</string>
<string name="fb_login_protocol_scheme">fb_login_protocol_scheme</string>

<string name="twitter_consumer_key" translatable="false">twitter_consumer_key</string>
<string name="twitter_consumer_secret" translatable="false">twitter_consumer_key</string>
```
- 楽天ブックス書籍検索API
API KEYを入れてください
```
package app.kojima.jiko.boox.model.services.api

import app.kojima.jiko.boox.model.entities.BookResponses
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {
    @GET("/services/api/BooksBook/Search/20170404")
    fun getBooks(@Query("applicationId") applicationId: String = "楽天書籍APIのapplication ID", @Query("title") title: String): Observable<BookResponses>
}
```
