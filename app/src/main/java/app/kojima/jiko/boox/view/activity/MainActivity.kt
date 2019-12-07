package app.kojima.jiko.boox.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.view.fragment.mypage.MypageFragment
import app.kojima.jiko.boox.view.fragment.setting.SettingFragment
import app.kojima.jiko.boox.view.fragment.top.TopBaseFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView.setOnNavigationItemSelectedListener {
            val activeFragment: Fragment = when(it.itemId) {
                R.id.bottom_nav_top -> TopBaseFragment()
                R.id.bottom_nav_mypage -> MypageFragment()
                else -> SettingFragment()
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.mainFrame, activeFragment)
                .commit()
            return@setOnNavigationItemSelectedListener true
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.mainFrame, TopBaseFragment())
            .commit()
    }
}
