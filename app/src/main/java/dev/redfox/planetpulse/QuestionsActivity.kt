package dev.redfox.planetpulse

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.redfox.planetpulse.databinding.ActivityQuestionsBinding
import dev.redfox.planetpulse.ui.question.Step1Fragment

class QuestionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionsBinding.inflate(layoutInflater)
//        replaceFragment(Step1Fragment())
        setContentView(binding.root)
    }
    override fun onResume() {
        super.onResume()
        var uri: Uri = Uri.parse("android.resource://" + "dev.redfox.planetpulse" + "/" + R.raw.chatback)
        binding.chatback.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        binding.chatback.setVideoURI(uri)
        binding.chatback.start()
        binding.chatback.setOnPreparedListener { it.isLooping = true }

    }

//    private fun replaceFragment(fragment: Fragment){
//
//        val fragmentManager = supportFragmentManager
//        val fragmentTransaction = fragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.frameLayout, fragment)
//        fragmentTransaction.commit()
//    }
}