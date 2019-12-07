package app.kojima.jiko.boox.model.services.firestore

import app.kojima.jiko.boox.model.entities.Comment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.reactivex.Completable
import io.reactivex.Single

object CommentService {
    private val db: FirebaseFirestore
    private val COLLECTION_PATH = "comments"

    init {
        db = FirebaseFirestore.getInstance()
    }

    fun getComments(bookId: String): Single<Map<String, Comment>> {
        return Single.create { emitter ->
            db.collection(COLLECTION_PATH)
                .whereEqualTo("bookId", bookId)
                .orderBy("created", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener {
                    val comments= hashMapOf<String, Comment>()
                    for(item in it) {
                        comments.set(item.id, item.toObject(Comment::class.java))
                    }
                    emitter.onSuccess(comments)
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun addComment(comment: Comment, uid: String, userName: String?, userIcon: String?, isbn: String): Completable {
        comment.bookId = isbn
        comment.userId = uid
        comment.userName = userName
        comment.usreIcon = userIcon
        return Completable.create { completableEmitter ->
            db.collection(COLLECTION_PATH)
                .add(comment)
                .addOnSuccessListener {
                    completableEmitter.onComplete()
                }
                .addOnFailureListener {
                    completableEmitter.onError(it)
                }
        }
    }
}