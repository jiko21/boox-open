package app.kojima.jiko.boox.view.fragment.user

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
import app.kojima.jiko.boox.view.activity.BookReviewViewModelActivity
import app.kojima.jiko.boox.view.adapter.BookCardAdapter
import app.kojima.jiko.boox.view.adapter.BookCardAdapterListener
import app.kojima.jiko.boox.viewmodel.BookReviewViewModel
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_user.*

class UserFragment : Fragment() {
    lateinit var viewModel: BookReviewViewModel
    lateinit var bookCardAdapter: BookCardAdapter
    private var listener: UserFragmentListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as BookReviewViewModelActivity).bookReviewViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.fetchUserBooks()
        viewModel.getUser().observe(viewLifecycleOwner, Observer {
            userBookUserName.text = it.name
            Glide.with(this).load(it.icon).into(userBookUserIcon)
        })
        bookCardAdapter = BookCardAdapter(listener = object: BookCardAdapterListener{
            override fun onClickItem(book: Book) {
                listener?.onBookClick(book)
            }

        })
        userBookRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = bookCardAdapter
        }

        viewModel.getBooks().observe(viewLifecycleOwner, Observer {
            bookCardAdapter.setBooks(it)
        })
        viewModel.isMySelf.observe(viewLifecycleOwner, Observer {
            followButton.visibility = if (it) View.GONE else View.VISIBLE
        })

        viewModel.isFollowed.observe(viewLifecycleOwner, Observer {
            if(it) {
                followButton.text = "フォロー解除"
            } else {
                followButton.text = "フォローする"
            }
        })

        followButton.setOnClickListener {
           listener?.onUpdateFolower()
        }
    }

    interface UserFragmentListener {
        fun onBookClick(book: Book)
        fun onUpdateFolower()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UserFragmentListener) {
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
        fun newInstance() = UserFragment()
    }
}
