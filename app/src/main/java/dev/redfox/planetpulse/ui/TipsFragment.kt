package dev.redfox.planetpulse.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.redfox.planetpulse.R
import dev.redfox.planetpulse.databinding.FragmentTipsBinding

class TipsFragment : Fragment() {

    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)

        // Set click listener for the Calculate card
        binding.cvThought.setOnClickListener {
            navigateToCalculator()
        }

        // Set up click listeners for tip cards if needed
        setupTipCardListeners()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display the saved carbon footprint
        displaySavedCarbonFootprint()
    }

    private fun setupTipCardListeners() {
        // Example for one card - repeat for others as needed
        binding.cvTip0.setOnClickListener {
            // Handle tip card click - perhaps show detailed information
        }
        // Add listeners for other tip cards
    }

    private fun displaySavedCarbonFootprint() {
        val sharedPreferences = requireActivity().getSharedPreferences("CarbonFootprintPrefs", Context.MODE_PRIVATE)
        val carbonFootprint = sharedPreferences.getFloat("carbon_footprint", 0f)

        if (carbonFootprint > 0) {
            // Add a TextView below tvCarbonFootprint if it doesn't exist
            // For this example, we'll modify the existing tvCarbonFootprint
            binding.tvCarbonFootprint.text = String.format("Your carbon footprint: %.2f tonnes CO₂/year\nLet's neutralize it!", carbonFootprint)
        } else {
            binding.tvCarbonFootprint.text = "Let's neutralize your carbon footprint!"
        }
    }

    private fun navigateToCalculator() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, CarbonCalculatorFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onResume() {
        super.onResume()

        // Refresh the displayed carbon footprint every time the fragment resumes
        displaySavedCarbonFootprint()

        // Set up and start the video
        val uri: Uri = Uri.parse("android.resource://dev.redfox.planetpulse/" + R.raw.tips)
        val params = binding.videoView.layoutParams
        params.height = resources.displayMetrics.heightPixels / 2
        binding.videoView.layoutParams = params
        binding.videoView.setVideoURI(uri)
        binding.videoView.start()
        binding.videoView.setOnPreparedListener { it.isLooping = true }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
