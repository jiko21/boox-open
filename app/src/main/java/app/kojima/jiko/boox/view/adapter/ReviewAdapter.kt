package app.kojima.jiko.boox.view.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.entities.Comment
import app.kojima.jiko.boox.model.entities.Like
import com.bumptech.glide.Glide
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.review_list.view.*

interface ReviewAdapterListener {
    fun onClickItem(userId: String)
    fun onLikeClicked(comment: Comment): Completable?
}

class ReviewAdapter(private var listener: ReviewAdapterListener? = null) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    private var data: Map<String, Comment> = HashMap()
    private var likes: Map<String, Like> = HashMap()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.review_list, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val likeList = likes.map { it.value }.filter { it.commentId == data.map { it.key }.get(position) }
        holder.bind(
            data.map { it.key }.get(position),
            data.map { it.value }.get(position),
            likeList.getOrNull(0),
            listener
        )
    }

    fun swapData(data: Map<String, Comment>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun setLikes(data: Map<String, Like>) {
        this.likes = data
        notifyDataSetChanged()
    }

    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(commentKey: String, item: Comment, like: Like?, listener: ReviewAdapterListener?) = with(itemView) {
            reviewContent.text = item.content
            reviewUserName.text = item.userName
            Glide.with(context).load(item.usreIcon).into(this.reviewIconImage)
            setOnClickListener {
                listener?.onClickItem(item.userId)
            }
            likeButton.isChecked = like != null
            likeButton.setOnClickListener {
                listener?.onLikeClicked(item)?.subscribe(object: CompletableObserver{
                    override fun onComplete() {
                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onError(e: Throwable) {
                        AlertDialog.Builder(context)
                            .setTitle("エラー")
                            .setMessage("自分のコメントにいいねはできません")
                            .setPositiveButton("OK", null)
                            .show()
                        likeButton.isChecked = like != null
                    }

                })
            }
        }
    }
}