package app.kojima.jiko.boox.model.services.firestore

import app.kojima.jiko.boox.model.entities.Follow
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.Single

object FollowService {
    private val db: FirebaseFirestore
    private val COLLECTION_PATH = "follows"

    init {
        db = FirebaseFirestore.getInstance()
    }

    fun updateFollows(uid: String, follow: Follow): Completable {
        return Completable.create { emitter ->
            db.collection(COLLECTION_PATH)
                .document(uid)
                .set(follow)
                .addOnSuccessListener {
                    emitter.onComplete()
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun getFollows(uid: String): Single<Follow> {
        return Single.create { emitter ->
            db.collection(COLLECTION_PATH)
                .document(uid)
                .get()
                .addOnSuccessListener {
                    val follow = it.toObject(Follow::class.java)
                    emitter.onSuccess(follow ?: Follow())
                }.addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

}