package dev.redfox.planetpulse.ui.question

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dev.redfox.planetpulse.R
import dev.redfox.planetpulse.databinding.FragmentStep1Binding
import dev.redfox.planetpulse.databinding.FragmentStep4Binding


class Step4Fragment : Fragment() {


    private var _binding: FragmentStep4Binding? = null
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStep4Binding.inflate(inflater, container, false)

        val firebaseDatabase: FirebaseDatabase
        firebaseDatabase = FirebaseDatabase.getInstance();

        val sharedPreference = activity?.getSharedPreferences("EMAIL", Context.MODE_PRIVATE)
        val email = sharedPreference?.getString("email", null)

        database =
            firebaseDatabase.getReference("employees").child(EncodeString(email!!).toString())

        binding.btnForward4.setOnClickListener {
            findNavController().navigate(R.id.action_step4Fragment_to_step5Fragment)
        }
        return binding.root
    }

    fun EncodeString(string: String): String? {
        return string.replace(".", ",")
    }

}