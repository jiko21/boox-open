package app.kojima.jiko.boox.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.entities.Book
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.book_card.view.*
import java.util.*

interface BookCardAdapterListener {
    fun onClickItem(book: Book)
}

class BookCardAdapter(val isVisibleFollower:Boolean = false, private var listener: BookCardAdapterListener? = null): RecyclerView.Adapter<BookCardAdapter.BookCard>() {

    private var data: List<Book> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookCard {
        return BookCard(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.book_card, parent, false),
            isVisibleFollower
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: BookCard, position: Int) = holder.bind(data[position], listener)

    fun setBooks(data: List<Book>) {
        this.data = data
        notifyDataSetChanged()
    }

    class BookCard(itemView: View, val isVisibleFollower:Boolean) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: Book, listener: BookCardAdapterListener?) = with(itemView) {
            this.followerGroup.visibility = if (isVisibleFollower) {
                View.VISIBLE
            } else {
                View.GONE
            }
            this.cardBookTitle.text = item.title
            this.cardBookAuthor.text = item.author
            Glide.with(context).load(item.thumb).into(this.cardBookImage)
            Glide.with(context).load(item.photoUrl).into(this.bookCardUserImage)
            this.bookCardUserName.text = "${item.userName}さんが読みました"
            setOnClickListener {
                listener?.onClickItem(item)
            }
        }
    }
}
