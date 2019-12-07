package app.kojima.jiko.boox.view.fragment.bookreview

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.entities.Book
import app.kojima.jiko.boox.model.entities.BookResponseError
import app.kojima.jiko.boox.view.activity.BookReviewActivity
import app.kojima.jiko.boox.view.adapter.BookCardAdapter
import app.kojima.jiko.boox.view.adapter.BookCardAdapterListener
import app.kojima.jiko.boox.view.view.ProgressCircle
import app.kojima.jiko.boox.viewmodel.BookReviewViewModel
import kotlinx.android.synthetic.main.fragment_book_search.*

class BookSearchFragment : Fragment() {
    lateinit var searchBookCardAdapter: BookCardAdapter
    lateinit var bookreviewViewModel: BookReviewViewModel
    private var listener: BookSearchFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bookreviewViewModel = (activity as BookReviewActivity).bookReviewViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_book_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchButton.setOnClickListener {
            val title = searchBox.text.toString()
            if (title.isNotBlank()) {
                val progressCircle = ProgressCircle()
                bookreviewViewModel.searchBooks(title)
                    ?.subscribe({
                    },
                        {
                            progressCircle.dismiss()
                            var msg = ""
                            if (it is BookResponseError) {
                                msg = "本が見つかりませんでした。"
                            } else {
                                msg = "通信でエラーが生じました。"
                            }
                            AlertDialog.Builder(context)
                                .setTitle("エラー")
                                .setMessage(msg)
                                .setPositiveButton("OK", null)
                                .show()
                        },
                        {
                            progressCircle.dismiss()
                        },
                        {
                            progressCircle.show(fragmentManager!!, "")
                        }
                    )
            }
        }
        searchBookCardAdapter = BookCardAdapter(listener = object : BookCardAdapterListener {
            override fun onClickItem(book: Book) {
                listener?.tapBook(book)
            }
        })
        bookSearchRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = searchBookCardAdapter
        }
        bookreviewViewModel.getBooks().observe(viewLifecycleOwner, Observer { books ->
            searchBookCardAdapter.setBooks(books)
        })
    }

    interface BookSearchFragmentListener {
        fun tapBook(book: Book)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BookSearchFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = BookSearchFragment()
    }
}
