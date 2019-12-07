package app.kojima.jiko.boox.view.activity

import android.app.AlertDialog
import android.os.Bundle
import androidx.navigation.Navigation
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.entities.Book
import app.kojima.jiko.boox.model.entities.Comment
import app.kojima.jiko.boox.view.fragment.bookreview.AddReviewFragment
import app.kojima.jiko.boox.view.fragment.bookreview.BookSearchFragment
import app.kojima.jiko.boox.view.view.ProgressCircle
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable

class BookReviewActivity :  BookSearchFragment.BookSearchFragmentListener, AddReviewFragment.AddReviewFragmentListener, BookReviewViewModelActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_review)
    }

    override fun onClickReviewItem(userId: String) {
    }

    override fun onSubmitComment(text: String) {
        val progressCircle = ProgressCircle()
        bookReviewViewModel.putBook(text)
            .subscribe(object: CompletableObserver {
                override fun onComplete() {
                    progressCircle.dismiss()
                    finish()
                }

                override fun onSubscribe(d: Disposable) {
                    progressCircle.show(supportFragmentManager, "")
                }

                override fun onError(e: Throwable) {
                    progressCircle.dismiss()
                    AlertDialog.Builder(applicationContext)
                        .setTitle("エラー")
                        .setMessage("申し訳ございません。コメントの投稿ができませんでした。")
                        .setPositiveButton("OK", null)
                        .show()
                }
            })
    }

    override fun onLikeClicked(comment: Comment): Completable {
        return bookReviewViewModel.updateLikes(comment).doOnComplete {
            bookReviewViewModel.fetchLikes()
        }
    }

    override fun tapBook(book: Book) {
        bookReviewViewModel.setBook(book)
        bookReviewViewModel.fetchComments()
        Navigation.findNavController(this, R.id.fragment)
            .navigate(R.id.action_bookSearchFragment_to_addReviewFragment)
    }
}
