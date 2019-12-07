package app.kojima.jiko.boox.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.entities.User
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.ranking_list.view.*
import java.util.*

interface RankingAdapterListener {
    fun onItemClick(item: User)
}
class RankingAdapter (val rankingAdapterListener: RankingAdapterListener? = null): RecyclerView.Adapter<RankingAdapter.RankingViewHolder>() {

    private var data: List<User> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        return RankingViewHolder(
            rankingAdapterListener,
            LayoutInflater.from(parent.context)
                .inflate(R.layout.ranking_list, parent, false)
        )
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) =
        holder.bind(data[position], position)

    fun setRanking(data: List<User>) {
        this.data = data
        notifyDataSetChanged()
    }

    class RankingViewHolder(val rankingAdapterListener: RankingAdapterListener? = null, itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: User, position: Int) = with(itemView) {
            Glide.with(this).load(item.icon).into(rankingUserIcon)
            rankingUserName.text = item.name
            rankingUserLikes.text = item.likes.toString()
            rankingUserRank.text = "${position + 1}"
            rankingUserRank.backgroundTintList = when(position){
                0 -> resources.getColorStateList(R.color.gold)
                1 -> resources.getColorStateList(R.color.silver)
                2 -> resources.getColorStateList(R.color.bronz)
                else -> resources.getColorStateList(R.color.rank)
            }
            setOnClickListener {
                rankingAdapterListener?.onItemClick(item)
            }
        }
    }
}