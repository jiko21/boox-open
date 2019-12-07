package app.kojima.jiko.boox.view.fragment.top

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import app.kojima.jiko.boox.R

class TopTapAdapter(fm: FragmentManager, val context: Context) : FragmentPagerAdapter(fm) {
    val titleArray = arrayOf(R.string.title_all, R.string.title_follow, R.string.title_ranking)
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> AllFragment()
        1 -> AllFragment().apply {
            arguments = Bundle().apply {
                putBoolean("FILTERED", true)
            }
        }
        else -> RankingFragment()
    }

    override fun getPageTitle(position: Int) = context.resources.getString(titleArray[position])

    override fun getCount() = titleArray.size
}