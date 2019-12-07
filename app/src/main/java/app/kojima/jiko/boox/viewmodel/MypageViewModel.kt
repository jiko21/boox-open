package app.kojima.jiko.boox.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.kojima.jiko.boox.model.entities.Book
import app.kojima.jiko.boox.model.services.firestore.BookService
import com.google.firebase.auth.FirebaseAuth
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MypageViewModel: ViewModel() {
    val compositeDisposable = CompositeDisposable()
    val books: MutableLiveData<List<Book>> = MutableLiveData()

    fun onResume() {
        fetchMyBooks()
    }

    fun fetchMyBooks() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val bookComposable = BookService.getBooks(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { books ->
                this.books.value = books
            }
        compositeDisposable.add(bookComposable)
    }

    fun getBooks(): LiveData<List<Book>> = this.books

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}