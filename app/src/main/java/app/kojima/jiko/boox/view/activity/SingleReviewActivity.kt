package app.kojima.jiko.boox.view.activity

import android.app.AlertDialog
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.entities.Book
import app.kojima.jiko.boox.model.entities.Comment
import app.kojima.jiko.boox.view.fragment.bookreview.AddReviewFragment
import app.kojima.jiko.boox.view.fragment.user.UserFragment
import app.kojima.jiko.boox.view.util.ComeFrom
import app.kojima.jiko.boox.view.view.ProgressCircle
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_single_review.*

class SingleReviewActivity : AddReviewFragment.AddReviewFragmentListener,UserFragment.UserFragmentListener, BookReviewViewModelActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_review)

        bookReviewViewModel.fetchFollower()
        bookReviewViewModel.fetchLikes()

        val comeFrom: ComeFrom= intent.getSerializableExtra("COME_FROM") as ComeFrom
        when(comeFrom) {
            ComeFrom.BOOK -> {
                val book = intent.getSerializableExtra("BOOK") as Book
                bookReviewViewModel.setBook(book)
                bookReviewViewModel.fetchComments()
            }
            ComeFrom.USER -> {
                val navHostFragment = fragment2 as NavHostFragment
                val inflater = navHostFragment.navController.navInflater
                val graph = inflater.inflate(R.navigation.single_review_navigation)
                graph.startDestination = R.id.userFragment
                navHostFragment.navController.graph = graph
                val userId = intent.getStringExtra("USERID")
                bookReviewViewModel.setUser(userId)
            }
        }

    }

    override fun onBookClick(book: Book) {
        bookReviewViewModel.setBook(book)
        bookReviewViewModel.fetchComments()
        Navigation.findNavController(this, R.id.fragment2)
            .navigate(R.id.action_userFragment_to_addReviewFragment2)
    }

    override fun onUpdateFolower() {
        bookReviewViewModel.updateFolower()
            .subscribe(object: CompletableObserver{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    AlertDialog.Builder(applicationContext)
                        .setTitle("エラー")
                        .setMessage("申し訳ございません。フォローができませんでした。")
                        .setPositiveButton("OK", null)
                        .show()
                }

            })
    }

    override fun onSubmitComment(text: String) {
        val progressCircle = ProgressCircle()
        bookReviewViewModel.putBook(text)
            .subscribe(object: CompletableObserver {
                override fun onComplete() {
                    progressCircle.dismiss()
                    bookReviewViewModel.fetchComments()
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

    override fun onClickReviewItem(userId: String) {
        bookReviewViewModel.setUser(userId)
        Navigation.findNavController(this, R.id.fragment2)
            .navigate(R.id.action_addReviewFragment2_to_userFragment)
    }
}
