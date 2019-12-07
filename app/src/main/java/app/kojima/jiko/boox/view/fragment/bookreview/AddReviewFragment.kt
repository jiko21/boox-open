package app.kojima.jiko.boox.view.fragment.bookreview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.entities.Comment
import app.kojima.jiko.boox.view.activity.BookReviewViewModelActivity
import app.kojima.jiko.boox.view.adapter.ReviewAdapter
import app.kojima.jiko.boox.view.adapter.ReviewAdapterListener
import app.kojima.jiko.boox.viewmodel.BookReviewViewModel
import com.bumptech.glide.Glide
import io.reactivex.Completable
import kotlinx.android.synthetic.main.fragment_add_review.*


class AddReviewFragment : Fragment() {
    lateinit var reviewAdapter: ReviewAdapter
    lateinit var bookreviewViewModel: BookReviewViewModel
    private var listener: AddReviewFragmentListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookreviewViewModel = (activity as BookReviewViewModelActivity).bookReviewViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reviewAdapter = ReviewAdapter(object : ReviewAdapterListener {
            override fun onLikeClicked(comment: Comment): Completable? {
                return listener?.onLikeClicked(comment)
            }

            override fun onClickItem(userId: String) {
                listener?.onClickReviewItem(userId)
            }

        })
        bookReviewRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reviewAdapter
        }
        bookreviewViewModel.getComments().observe(viewLifecycleOwner, Observer {
            reviewAdapter.swapData(it)
        })
        bookreviewViewModel.getLikes().observe(viewLifecycleOwner, Observer {
            reviewAdapter.setLikes(it)
        })
        submitButton.setOnClickListener {
            if (commentEdit.text!!.isNotBlank()) {
                listener?.onSubmitComment(commentEdit.text.toString())
            }
        }

        bookreviewViewModel.getBook().observe(viewLifecycleOwner, Observer {book->
            bookReviewAuthor.text = book.author
            bookReviewTitle.text = book.title
            Glide.with(this).load(book.thumb).into(bookReviewImage)
            rakutenBooksText.setOnClickListener {
                book.itemUrl?.also {
                    val uri = Uri.parse(book.itemUrl)
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            }

        })
        bookreviewViewModel.alreadyCommented().observe(viewLifecycleOwner, Observer {
            if(it) {
                addReviewGroup.visibility = View.GONE
            } else {
                addReviewGroup.visibility = View.VISIBLE
            }
        })

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddReviewFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface AddReviewFragmentListener {
        fun onClickReviewItem(userId: String)
        fun onSubmitComment(text: String)
        fun onLikeClicked(comment: Comment): Completable
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddReviewFragment()
    }
}
