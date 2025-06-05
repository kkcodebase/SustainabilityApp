package dev.redfox.planetpulse

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import dev.redfox.planetpulse.ui.RewardFragment
import com.google.firebase.FirebaseApp
import dev.redfox.planetpulse.databinding.ActivityMainBinding
import dev.redfox.planetpulse.ui.DashboardFragment
import dev.redfox.planetpulse.ui.GptFragment
import dev.redfox.planetpulse.ui.ShopFragment
import dev.redfox.planetpulse.ui.TipsFragment

class MainActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        FirebaseApp.initializeApp(this)
        replaceFragment(DashboardFragment())

        window.setStatusBarColor(this.getResources().getColor(R.color.color_appBar))
        binding.bottomNav.setOnItemSelectedListener {

            when(it.itemId) {
                R.id.dashboard -> replaceFragment(DashboardFragment())
                R.id.community -> replaceFragment(GptFragment())
                R.id.rewards -> replaceFragment(RewardFragment())
                R.id.shop -> replaceFragment(ShopFragment())
                R.id.tips -> replaceFragment(TipsFragment())
                else -> {}
            }
            true
        }
        setContentView(binding.root)
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }
}
