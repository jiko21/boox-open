package app.kojima.jiko.boox.view.fragment.top

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.viewmodel.TopViewModel
import kotlinx.android.synthetic.main.fragment_top_base.*

class TopBaseFragment : Fragment() {
    val viewModel by lazy { ViewModelProviders.of(this).get(TopViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_base, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentManager?.also { fragmentManager ->
            context?.also {context ->
                topTabPager.adapter = TopTapAdapter(childFragmentManager, context)
                topTabLayout.setupWithViewPager(topTabPager)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    companion object {
        @JvmStatic
        fun newInstance() = TopBaseFragment()
    }
}
