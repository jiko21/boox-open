package app.kojima.jiko.boox.view.fragment.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.view.activity.LoginActivity
import app.kojima.jiko.boox.view.adapter.SettingAdapter
import com.bumptech.glide.Glide
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {

    val firebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val user = firebaseAuth.currentUser ?:return@onViewCreated
        Glide.with(this).load(user.photoUrl).into(settingIconView)
        settingUserName.text = user.displayName
        val list = resources.getStringArray(R.array.setting_list).toList()
        context?.also {

            val settingAdapter = SettingAdapter(it, list)
            setttingListView.adapter = settingAdapter
            setttingListView.setOnItemClickListener { adapterView, view, i, l ->
                when(i) {
                    0 ->{
                        val intent = Intent(context, OssLicensesMenuActivity::class.java)
                        intent.putExtra("title", "OSSライセンス")
                        startActivity(intent)
                    }
                    1 -> {
                        val uri = Uri.parse("https://boox-app.tumblr.com/terns")
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                    2 -> {
                        val uri = Uri.parse("https://boox-app.tumblr.com/privacy")
                        startActivity(Intent(Intent.ACTION_VIEW, uri))
                    }
                    else -> {
                        firebaseAuth?.signOut()
                        val intent = Intent(context, LoginActivity::class.java)
                        startActivity(intent)
                        activity?.finish()
                    }
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingFragment()
    }
}
