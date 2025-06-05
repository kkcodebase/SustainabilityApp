package dev.redfox.planetpulse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import dev.redfox.planetpulse.auth.LoginActivity
import dev.redfox.planetpulse.databinding.ActivityGetStartedBinding
import dev.redfox.planetpulse.databinding.ActivityMainBinding

@SuppressLint("StaticFieldLeak")
private lateinit var binding: ActivityGetStartedBinding

class GetStartedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            binding.tapHere.visibility = ViewGroup.VISIBLE
            val animation = AnimationUtils.loadAnimation(
                this, R.anim.slide_up)
            //appending animation to textView
            binding.tapHere.startAnimation(animation)
        }, 2000)

        binding.tapHere.setOnClickListener {
            binding.btnGetStarted.visibility = View.VISIBLE
            binding.tapHere.visibility = View.INVISIBLE
        }

        binding.btnGetStarted.setOnClickListener {

            // ❗FOR TESTING: This clears login state every time to show login page
            getSharedPreferences("LOGIN", Context.MODE_PRIVATE).edit().clear().apply()

            val sharedPreference =
                getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
            val checkLogin: Boolean = sharedPreference.getBoolean("hasLoggedIn", false)

            if (checkLogin == true) {
                val intent = Intent(this, QuestionsActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val uri: Uri = Uri.parse("android.resource://" + packageName + "/" + R.raw.videointro)
        binding.videoView.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        binding.videoView.setVideoURI(uri)
        binding.videoView.start()
        binding.videoView.setOnPreparedListener { it.isLooping = true }
    }
}
