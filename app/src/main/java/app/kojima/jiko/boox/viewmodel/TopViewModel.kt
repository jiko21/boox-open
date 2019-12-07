package app.kojima.jiko.boox.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.kojima.jiko.boox.model.entities.Book
import app.kojima.jiko.boox.model.entities.Follow
import app.kojima.jiko.boox.model.entities.User
import app.kojima.jiko.boox.model.services.firestore.BookService
import app.kojima.jiko.boox.model.services.firestore.FollowService
import app.kojima.jiko.boox.model.services.firestore.UserService
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TopViewModel: ViewModel() {
    val compositeDisposable = CompositeDisposable()
    val books: MediatorLiveData<List<Book>> = MediatorLiveData()
    var follower: MutableLiveData<Follow> = MutableLiveData()
    val userRanks = MutableLiveData<List<User>>()

    fun onResume() {
        Log.d("onResume","onResume")
        fetchFollower()
        fetchAllBooks()
    }

    fun fetchAllBooks(filtered: Boolean = false) {
        val bookServiceDisposable = BookService.getAllBooks()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { books ->
                this.books.value = books
            }
        compositeDisposable.add(bookServiceDisposable)
    }
    fun fetchFollower() {
        val followServiceDisposable = FollowService.getFollows(FirebaseAuth.getInstance().currentUser!!.uid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it->
                follower.value = it
            }
        compositeDisposable.add(followServiceDisposable)
    }

    fun getFollower(): LiveData<Follow> = follower

    fun getAllBooks(): LiveData<List<Book>> = books

    fun fetchUsers() {
        val usersDisposable = UserService.getUsers()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { it ->
                userRanks.value = it
            }
        compositeDisposable.add(usersDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
