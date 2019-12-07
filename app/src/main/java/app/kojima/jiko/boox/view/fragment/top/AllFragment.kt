package app.kojima.jiko.boox.view.fragment.top

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.entities.Book
import app.kojima.jiko.boox.view.activity.SingleReviewActivity
import app.kojima.jiko.boox.view.adapter.BookCardAdapter
import app.kojima.jiko.boox.view.adapter.BookCardAdapterListener
import app.kojima.jiko.boox.view.util.ComeFrom
import kotlinx.android.synthetic.main.fragment_all.*
private const val ARG_PARAM = "FILTERED"

class AllFragment : Fragment() {
    lateinit var bookCardAdapter: BookCardAdapter
    private var filtered: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            filtered = it.getBoolean(ARG_PARAM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookCardAdapter =
            BookCardAdapter(isVisibleFollower = true, listener = object : BookCardAdapterListener {
                override fun onClickItem(book: Book) {
                    jumpToSingleReview(book)
                }

            })
        followBookRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookCardAdapter
        }
        (parentFragment as TopBaseFragment).viewModel.getAllBooks().observe(viewLifecycleOwner, Observer {books ->
            if (books != null) {
                if(filtered) {
                    (parentFragment as TopBaseFragment).viewModel.getFollower()
                        .observe(viewLifecycleOwner, Observer { follower ->
                            bookCardAdapter.setBooks(books.filter {
                                follower.followList.containsKey(it.userId)
                            })
                        })
                } else {
                    bookCardAdapter.setBooks(books)
                }
            }
        })
    }

    fun jumpToSingleReview(book: Book) {
        val intent = Intent(context, SingleReviewActivity::class.java)
        intent.putExtra("BOOK", book)
        intent.putExtra("COME_FROM", ComeFrom.BOOK)
        startActivity(intent)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Boolean) = AllFragment().apply {
            arguments = Bundle().apply {
                putBoolean(ARG_PARAM, param1)
            }
        }
    }
}
