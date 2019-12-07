package app.kojima.jiko.boox.view.fragment.top

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
import app.kojima.jiko.boox.model.entities.User
import app.kojima.jiko.boox.view.activity.SingleReviewActivity
import app.kojima.jiko.boox.view.adapter.RankingAdapter
import app.kojima.jiko.boox.view.adapter.RankingAdapterListener
import app.kojima.jiko.boox.view.util.ComeFrom
import app.kojima.jiko.boox.viewmodel.TopViewModel
import kotlinx.android.synthetic.main.fragment_ranking.*

class RankingFragment : Fragment() {
    val viewModel by lazy { ViewModelProviders.of(this).get(TopViewModel::class.java) }

    lateinit var rankingAdapter: RankingAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.fetchUsers()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ranking, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rankingAdapter = RankingAdapter(object: RankingAdapterListener {
            override fun onItemClick(item: User) {
                jumpToSingleReview(item.uid)
            }

        })
        rankingRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rankingAdapter
        }
        viewModel.userRanks.observe(viewLifecycleOwner, Observer {
            rankingAdapter.setRanking(it)
        })
    }
    fun jumpToSingleReview(userId: String) {
        val intent = Intent(context, SingleReviewActivity::class.java)
        intent.putExtra("USERID", userId)
        intent.putExtra("COME_FROM", ComeFrom.USER)
        startActivity(intent)
    }


    companion object {
        @JvmStatic
        fun newInstance() = RankingFragment()
    }
}
