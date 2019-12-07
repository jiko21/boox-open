package app.kojima.jiko.boox.model.services.firestore

import app.kojima.jiko.boox.model.entities.Like
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Single

object LikeService {
    private val db: FirebaseFirestore
    private val COLLECTION_PATH = "likes"

    init {
        db = FirebaseFirestore.getInstance()
    }

    fun addLike(like: Like): Completable {
        return Completable.create { completableEmitter ->
            db.collection(COLLECTION_PATH)
                .add(like)
                .addOnSuccessListener {
                    completableEmitter.onComplete()
                }
                .addOnFailureListener{
                    completableEmitter.onError(it)
                }
        }
    }

    fun deleteLike(id: String): Completable{
        return Completable.create {emitter->
            db.collection(COLLECTION_PATH)
                .document(id)
                .delete()
                .addOnSuccessListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun getLikes(userId: String): Single<Map<String, Like>> {
        return Single.create {emitter ->
            db.collection(COLLECTION_PATH)
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener {
                    val map = hashMapOf<String, Like>()
                    for(item in it) {
                        map.set(item.id,item.toObject(Like::class.java))
                    }
                    emitter.onSuccess(map)
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }
}