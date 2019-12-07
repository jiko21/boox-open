package app.kojima.jiko.boox.model.services.firestore

import app.kojima.jiko.boox.model.entities.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import io.reactivex.Completable
import io.reactivex.Single

object UserService {
    private val db: FirebaseFirestore
    private val COLLECTION_PATH = "users"

    init {
        db = FirebaseFirestore.getInstance()
    }

    fun createUser(firebaseUser: FirebaseUser): Completable {
        val user = User(name = firebaseUser.displayName ?: "", uid = firebaseUser.uid, icon = firebaseUser.photoUrl.toString())
        return Completable.create { completableEmitter ->
            db.collection(COLLECTION_PATH)
                .document(user.uid)
                .set(user, SetOptions.merge())
                .addOnSuccessListener {
                    completableEmitter.onComplete()
                }
                .addOnFailureListener {
                    completableEmitter.onError(it)
                }
        }
    }

    fun getUser(userId: String): Single<User?> {
        return Single.create {emitter ->
            db.collection(COLLECTION_PATH)
                .document(userId)
                .get()
                .addOnSuccessListener {
                    emitter.onSuccess(it.toObject(User::class.java)!!)
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }

    fun getUsers(): Single<List<User>> {
        return Single.create {emitter ->
            db.collection(COLLECTION_PATH)
                .orderBy("likes", Query.Direction.DESCENDING)
                .limit(10L)
                .get()
                .addOnSuccessListener {
                    val users = arrayListOf<User>()
                    for(user in it) {
                        users.add(user.toObject(User::class.java))
                    }
                    emitter.onSuccess(users)
                }
                .addOnFailureListener {
                    emitter.onError(it)
                }
        }
    }
}