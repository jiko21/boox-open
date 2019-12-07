package app.kojima.jiko.boox.view.fragment.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.entities.Book
import app.kojima.jiko.boox.view.activity.BookReviewActivity
import app.kojima.jiko.boox.view.activity.SingleReviewActivity
import app.kojima.jiko.boox.view.adapter.BookCardAdapter
import app.kojima.jiko.boox.view.adapter.BookCardAdapterListener
import app.kojima.jiko.boox.view.util.ComeFrom
import app.kojima.jiko.boox.viewmodel.MypageViewModel
import kotlinx.android.synthetic.main.fragment_mypage.*

class MypageFragment : Fragment() {
    lateinit var myBookCardAdapter: BookCardAdapter
    private val viewModel by lazy { ViewModelProviders.of(this).get(MypageViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addBookButton.setOnClickListener {
            val intent = Intent(context, BookReviewActivity::class.java)
            startActivity(intent)
        }
        myBookCardAdapter = BookCardAdapter(listener = object: BookCardAdapterListener{
            override fun onClickItem(book: Book) {
                val intent = Intent(context, SingleReviewActivity::class.java)
                intent.putExtra("BOOK", book)
                intent.putExtra("COME_FROM", ComeFrom.BOOK)
                startActivity(intent)
            }

        })
        myBookRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = myBookCardAdapter
        }
        viewModel.getBooks().observe(viewLifecycleOwner, Observer {
            myBookCardAdapter.setBooks(it)
        })
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance() = MypageFragment()

    }
}
