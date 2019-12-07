package app.kojima.jiko.boox.view.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import app.kojima.jiko.boox.viewmodel.BookReviewViewModel

open class BookReviewViewModelActivity : AppCompatActivity() {
    val bookReviewViewModel by lazy { ViewModelProviders.of(this).get(BookReviewViewModel::class.java) }
}
