package app.kojima.jiko.boox.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.entities.User
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.follow_card.view.*
import java.util.*
interface FollowerAdapterListener {
    fun onClick(item: User)
}

class FollowerAdapter(val followerAdapterListener: FollowerAdapterListener? = null): RecyclerView.Adapter<FollowerAdapter.FollowerCard>() {

    private var data: List<User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowerCard {
        return FollowerCard(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.follow_card, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: FollowerCard, position: Int) = holder.bind(data[position], followerAdapterListener)

    fun swapData(data: List<User>) {
        this.data = data
        notifyDataSetChanged()
    }

    class FollowerCard(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: User, followerAdapterListener: FollowerAdapterListener?) = with(itemView) {
            followCardUserName.text = item.name
            Glide.with(context).load(item.icon).into(followCardUserIcon)
            setOnClickListener {
                followerAdapterListener?.onClick(item)
            }
        }
    }
}