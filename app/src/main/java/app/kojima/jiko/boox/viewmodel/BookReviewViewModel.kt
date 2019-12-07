package app.kojima.jiko.boox.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.kojima.jiko.boox.model.entities.*
import app.kojima.jiko.boox.model.services.api.BookApiClient
import app.kojima.jiko.boox.model.services.firestore.*
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class BookReviewViewModel : ViewModel() {
    val compositeDisposable = CompositeDisposable()
    val books = MutableLiveData<List<Book>>()
    val book = MutableLiveData<Book>()
    val comments = MutableLiveData<Map<String, Comment>>()
    var userId: String = ""
    val user = MutableLiveData<User>()
    val isFollowed = MediatorLiveData<Boolean>()
    val follower = MutableLiveData<Follow>()
    val likes = MutableLiveData<Map<String, Like>>()
    val myId = FirebaseAuth.getInstance().currentUser!!.uid
    val isAlreadyCommented = MutableLiveData<Boolean>()
    val isMySelf = MutableLiveData<Boolean>()

    init{
        isFollowed.addSource(follower, {
            if (it.followList.containsKey(userId)) {
                isFollowed.value = true
            } else {
                isFollowed.value = false
            }
        })
    }


    fun fetchLikes() {
        val likeDisposable = LikeService.getLikes(myId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                likes.value = it
            }
        compositeDisposable.add(likeDisposable)
    }

    fun getLikes(): LiveData<Map<String, Like>> = likes

    fun updateLikes(comment: Comment): Completable {
        if(comment.userId == myId) {
            return Completable.create {
                it.onError(Error("User cannot like his/her own comment"))
            }
        }
        val commentId = comments.value?.filter { it.value == comment }?.keys?.first()
        var likeId = likes.value?.filter { it.value.commentId.equals(commentId) }?.keys?.toList()
            ?.getOrNull(0)
        if (likeId == null) {
            val like = Like(
                commentId = commentId!!,
                bookId = comments.value?.get(commentId)!!.bookId,
                userOfComment = comments.value?.get(commentId)!!.userId,
                userId = myId
            )
            return LikeService.addLike(like)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        } else {
            return LikeService.deleteLike(likeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }


    fun searchBooks(title: String): Observable<BookResponses>? {
        return BookApiClient.getBookService().getBooks(title = title)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnNext {
                books.value = it.items.map {
                    it.book
                }
                if(it.items.size == 0) {throw BookResponseError()}
            }
    }

    fun getBooks(): LiveData<List<Book>> = books
    fun getBook(): LiveData<Book> = book
    fun setBook(bookItem: Book) {
        book.value = bookItem
    }

    fun putBook(content: String): Completable {
        book.value!!.userId = FirebaseAuth.getInstance().currentUser!!.uid
        book.value!!.userName = FirebaseAuth.getInstance().currentUser!!.displayName
        book.value!!.photoUrl = FirebaseAuth.getInstance().currentUser!!.photoUrl.toString()
        return BookService.putBooks(book.value!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .andThen(addComment(content, book.value!!))
    }

    fun getComments(): LiveData<Map<String, Comment>> = comments

    fun setUser(userId: String) {
        this.userId = userId
        isMySelf.value = this.userId == myId
        val userDisposable = UserService.getUser(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                user.value = it
                if (follower.value?.followList?.containsKey(userId) ?: false) {
                    isFollowed.value = true
                }
            }
        compositeDisposable.add(userDisposable)
    }

    fun getUser(): LiveData<User> = user

    fun fetchUserBooks() {
        val bookCompositeDisposable = BookService.getBooks(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { books ->
                this.books.value = books
            }
        compositeDisposable.add(bookCompositeDisposable)
    }

    fun fetchComments() {
        val book = book.value ?: return
        val commentDisposable = CommentService.getComments(book.isbn)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                comments.value = it
                isAlreadyCommented.value = comments.value?.filterValues { it.userId == myId }?.isNotEmpty()
            }
        compositeDisposable.add(commentDisposable)
    }

    fun addComment(content: String, book: Book): Completable {
        val comment = Comment(content = content)
        return CommentService.addComment(
            comment,
            FirebaseAuth.getInstance().currentUser!!.uid,
            FirebaseAuth.getInstance().currentUser!!.displayName,
            FirebaseAuth.getInstance().currentUser!!.photoUrl.toString(),
            book.isbn
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchFollower() {
        val followDisposable =
            FollowService.getFollows(FirebaseAuth.getInstance().currentUser!!.uid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    follower.value = it
                }
        compositeDisposable.add(followDisposable)
    }

    fun updateFolower(): Completable {
        if (follower.value!!.followList.containsValue(user.value!!)) {
            follower.value!!.followList.remove(user.value!!.uid)
            isFollowed.value = false
        } else {
            follower.value!!.followList.put(user.value!!.uid, user.value!!)
            isFollowed.value = true
        }
        return FollowService.updateFollows(
            FirebaseAuth.getInstance().currentUser!!.uid,
            follower.value!!
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    fun alreadyCommented(): LiveData<Boolean> = isAlreadyCommented

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}