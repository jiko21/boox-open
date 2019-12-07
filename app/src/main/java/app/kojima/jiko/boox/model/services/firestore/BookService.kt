package app.kojima.jiko.boox.model.services.firestore

import app.kojima.jiko.boox.model.entities.Book
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.reactivex.Completable
import io.reactivex.Single

object BookService {
    private val db: FirebaseFirestore
    private val COLLECTION_PATH = "books"

    init {
        db = FirebaseFirestore.getInstance()
    }

    fun putBooks(book: Book): Completable {
        return Completable.create { completableEmitter ->
            db.collection(COLLECTION_PATH)
                .add(book)
                .addOnSuccessListener {
                    completableEmitter.onComplete()
                }
                .addOnFailureListener {
                    completableEmitter.onError(it)
                }
        }
    }

    fun getBooks(uid: String): Single<List<Book>> {
        return Single.create<List<Book>> { singleEmitter ->
            db.collection(COLLECTION_PATH)
                .whereEqualTo("userId", uid)
                .orderBy("created", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { result ->
                    val books: ArrayList<Book> = arrayListOf()
                    for(item in result) {
                        val book = item.toObject(Book::class.java)
                        books.add(book)
                    }
                    singleEmitter.onSuccess(books)
                }.addOnFailureListener {
                    singleEmitter.onError(it)
                }
        }
    }

    fun getAllBooks(): Single<List<Book>> {
        return Single.create<List<Book>> { singleEmitter ->
            db.collection(COLLECTION_PATH)
                .orderBy("created", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener { result ->
                    val books: ArrayList<Book> = arrayListOf()
                    for(item in result) {
                        val book = item.toObject(Book::class.java)
                        books.add(book)
                    }
                    singleEmitter.onSuccess(books)
                }.addOnFailureListener {
                    singleEmitter.onError(it)
                }
        }
    }
}