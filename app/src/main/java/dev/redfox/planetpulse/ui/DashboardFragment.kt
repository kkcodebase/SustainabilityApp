package dev.redfox.planetpulse.ui

import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dev.redfox.planetpulse.R
import dev.redfox.planetpulse.databinding.FragmentDashboardBinding


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        // Set default progress values
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            binding.indiaProgressBar.setProgress(50, true)
            binding.worldProgressBar.setProgress(50, true)
        } else {
            binding.indiaProgressBar.progress = 50
            binding.worldProgressBar.progress = 50
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load and display the saved carbon footprint
        displaySavedCarbonFootprint()
    }

    private fun displaySavedCarbonFootprint() {
        val sharedPreferences = requireActivity().getSharedPreferences("CarbonFootprintPrefs", Context.MODE_PRIVATE)
        val carbonFootprint = sharedPreferences.getFloat("carbon_footprint", 0f)

        if (carbonFootprint > 0) {
            // Update the carbon footprint value
            binding.tvCarbonFootprint.text = String.format("%.2f", carbonFootprint)

            // Calculate and update progress bars based on the carbon footprint
            // Average values for comparison
            val avgIndia = 2.33f // Average carbon footprint for India in tons
            val avgWorld = 4.50f // Average carbon footprint for the world in tons

            // Calculate progress percentages (capped at 100)
            val userProgress = (carbonFootprint / 10f * 100).toInt().coerceAtMost(100)

            // Update progress bars
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                binding.myProgressBar.setProgress(userProgress, true)
            } else {
                binding.myProgressBar.progress = userProgress
            }

            // Update text values
            binding.tvYouCO2Value.text = String.format("%.2f tons", carbonFootprint)

            // Update bar graph based on the components of carbon footprint
            // For this example, we'll use placeholder values from SharedPreferences
            updateBarGraph(sharedPreferences)
        }
    }

    private fun updateBarGraph(sharedPreferences: android.content.SharedPreferences) {
        // Get component values from SharedPreferences or use default distribution
        val totalFootprint = sharedPreferences.getFloat("carbon_footprint", 5.87f)

        // For this example, we'll calculate component values based on typical distribution
        // In a real app, you would save these individual components during calculation
        val flying = totalFootprint * 0.35f  // 35% from flying
        val mobility = totalFootprint * 0.25f // 25% from mobility
        val housing = totalFootprint * 0.20f  // 20% from housing
        val diet = totalFootprint * 0.10f     // 10% from diet
        val spending = totalFootprint * 0.10f // 10% from spending

        // Update the text values
        binding.tvFlyingVal.text = String.format("%.2f t", flying)
        binding.tvMobilityVal.text = String.format("%.2f t", mobility)
        binding.tvHousingVal.text = String.format("%.2f t", housing)
        binding.tvDietVal.text = String.format("%.2f t", diet)
        binding.tvSpendingVal.text = String.format("%.2f t", spending)

        // Update the bar heights proportionally
        // Maximum height is 150dp (the height of the first bar)
        val maxHeight = 150

        // Find the maximum component to scale others relative to it
        val maxComponent = maxOf(flying, mobility, housing, diet, spending)

        // Calculate and set heights for each bar
        val barHeights = listOf(
            (flying / maxComponent * maxHeight).toInt(),
            (mobility / maxComponent * maxHeight).toInt(),
            (housing / maxComponent * maxHeight).toInt(),
            (diet / maxComponent * maxHeight).toInt(),
            (spending / maxComponent * maxHeight).toInt()
        )

        // Get references to all bars in the LinearLayout
        val barGraph = binding.barGraph
        if (barGraph.childCount >= 5) {
            for (i in 0 until 5) {
                val cardView = barGraph.getChildAt(i) as androidx.cardview.widget.CardView
                val params = cardView.layoutParams
                params.height = barHeights[i].coerceAtLeast(10) // Ensure minimum height of 10dp
                cardView.layoutParams = params
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Refresh the carbon footprint display when returning to this fragment
        displaySavedCarbonFootprint()

        // Set up and play the background video
        var uri: Uri = Uri.parse("android.resource://" + "dev.redfox.planetpulse" + "/" + R.raw.videodashboard)
        binding.dashboardVideo.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        binding.dashboardVideo.setVideoURI(uri)
        binding.dashboardVideo.start()
        binding.dashboardVideo.setOnPreparedListener { it.isLooping = true }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
