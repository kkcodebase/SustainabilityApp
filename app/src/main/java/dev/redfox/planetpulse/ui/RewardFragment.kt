package dev.redfox.planetpulse.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.redfox.planetpulse.R
import dev.redfox.planetpulse.databinding.ActivityItemBinding
import dev.redfox.planetpulse.databinding.DialogLogActivityBinding
import dev.redfox.planetpulse.databinding.FragmentRewardBinding

class RewardFragment : Fragment() {

    private var _binding: FragmentRewardBinding? = null
    private val binding get() = _binding!!

    private lateinit var activitiesAdapter: SustainableActivitiesAdapter
    private val sustainableActivities = mutableListOf<SustainableActivity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRewardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the user's points from SharedPreferences
        updatePointsDisplay()

        // Set up the RecyclerView with activities
        setupRecyclerView()

        // Set up button click listeners
        binding.btnShopNow.setOnClickListener {
            Toast.makeText(context, "Shop feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        binding.fabLogActivity.setOnClickListener {
            showLogActivityDialog()
        }
    }

    private fun updatePointsDisplay() {
        val sharedPreferences = requireActivity().getSharedPreferences("EcoPointsPrefs", Context.MODE_PRIVATE)
        val points = sharedPreferences.getInt("eco_points", 0)

        binding.tvPointsValue.text = points.toString()

        // Update level information based on points
        updateLevelInfo(points)
    }

    private fun updateLevelInfo(points: Int) {
        val level = when {
            points < 100 -> 1
            points < 300 -> 2
            points < 750 -> 3
            points < 1500 -> 4
            else -> 5
        }

        val levelTitle = when(level) {
            1 -> "Eco Beginner"
            2 -> "Eco Enthusiast"
            3 -> "Eco Warrior"
            4 -> "Eco Champion"
            else -> "Eco Master"
        }

        binding.tvCurrentLevel.text = "Level $level: $levelTitle"

        // Calculate progress to next level
        val nextLevelThreshold = when(level) {
            1 -> 100
            2 -> 300
            3 -> 750
            4 -> 1500
            else -> 3000
        }

        val currentLevelThreshold = when(level) {
            1 -> 0
            2 -> 100
            3 -> 300
            4 -> 750
            else -> 1500
        }

        if (level < 5) {
            val pointsToNextLevel = nextLevelThreshold - points
            binding.tvNextLevel.text = "$pointsToNextLevel points to Level ${level + 1}"

            // Calculate progress percentage
            val levelRange = nextLevelThreshold - currentLevelThreshold
            val progress = ((points - currentLevelThreshold) * 100) / levelRange
            binding.pbLevelProgress.progress = progress
        } else {
            binding.tvNextLevel.text = "Maximum Level Reached!"
            binding.pbLevelProgress.progress = 100
        }
    }

    private fun setupRecyclerView() {
        // Initialize sample sustainable activities
        initializeSampleActivities()

        activitiesAdapter = SustainableActivitiesAdapter(
            sustainableActivities,
            onLogClicked = { activity ->
                logActivity(activity)
            }
        )

        binding.rvActivities.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = activitiesAdapter
        }
    }

    private fun initializeSampleActivities() {
        sustainableActivities.clear()
        sustainableActivities.addAll(
            listOf(
                SustainableActivity(
                    id = 1,
                    name = "Walk or Cycle to Work",
                    description = "Reduce carbon emissions by choosing eco-friendly transportation",
                    pointsReward = 50,
                    iconResId = R.drawable.baseline_directions_run_24
                ),
                SustainableActivity(
                    id = 2,
                    name = "Use Public Transportation",
                    description = "Reduce your carbon footprint by sharing your ride",
                    pointsReward = 30,
                    iconResId = R.drawable.baseline_directions_bus_24
                ),
                SustainableActivity(
                    id = 3,
                    name = "Plant a Tree",
                    description = "Help absorb CO2 and increase biodiversity",
                    pointsReward = 100,
                    iconResId = R.drawable.baseline_forest_24
                ),
                SustainableActivity(
                    id = 4,
                    name = "Zero Waste Day",
                    description = "Go a full day without producing any waste",
                    pointsReward = 75,
                    iconResId = R.drawable.baseline_recycling_24
                ),
                SustainableActivity(
                    id = 5,
                    name = "Meatless Monday",
                    description = "Reduce your carbon footprint by eating plant-based for a day",
                    pointsReward = 40,
                    iconResId = R.drawable.baseline_fastfood_24
                ),
                SustainableActivity(
                    id = 6,
                    name = "Energy Saving",
                    description = "Reduce electricity usage by 10% compared to last month",
                    pointsReward = 60,
                    iconResId = R.drawable.baseline_light_24
                ),
                SustainableActivity(
                    id = 7,
                    name = "Reusable Bottle",
                    description = "Use a reusable water bottle instead of single-use plastic",
                    pointsReward = 20,
                    iconResId = R.drawable.baseline_delete_outline_24
                ),
                SustainableActivity(
                    id = 8,
                    name = "Local Shopping",
                    description = "Buy locally produced goods to reduce transportation emissions",
                    pointsReward = 35,
                    iconResId = R.drawable.baseline_shopping_cart_checkout_24
                )
            )
        )
    }

    private fun logActivity(activity: SustainableActivity) {
        // Add points to user's total
        val sharedPreferences = requireActivity().getSharedPreferences("EcoPointsPrefs", Context.MODE_PRIVATE)
        val currentPoints = sharedPreferences.getInt("eco_points", 0)
        val newPoints = currentPoints + activity.pointsReward

        // Save the new points total
        sharedPreferences.edit().putInt("eco_points", newPoints).apply()

        // Log the activity in history
        val activityHistory = sharedPreferences.getStringSet("activity_history", mutableSetOf()) ?: mutableSetOf()
        val timestamp = System.currentTimeMillis()
        val logEntry = "${activity.id}|${activity.name}|${activity.pointsReward}|$timestamp"
        activityHistory.add(logEntry)
        sharedPreferences.edit().putStringSet("activity_history", activityHistory).apply()

        // Update the UI
        updatePointsDisplay()

        // Show success message
        Toast.makeText(
            requireContext(),
            "Great job! You earned ${activity.pointsReward} points for ${activity.name}",
            Toast.LENGTH_SHORT
        ).show()

        // Update carbon footprint reduction
        updateCarbonFootprintReduction(activity)
    }

    private fun updateCarbonFootprintReduction(activity: SustainableActivity) {
        // Calculate carbon footprint reduction based on activity
        val carbonReduction = when (activity.id) {
            1 -> 0.05f // Walking/cycling saves ~0.05 tons CO2 per week
            2 -> 0.03f // Public transport saves ~0.03 tons CO2 per week
            3 -> 0.1f  // Planting a tree absorbs ~0.1 tons CO2 per year
            4 -> 0.02f // Zero waste day saves ~0.02 tons CO2
            5 -> 0.04f // Meatless day saves ~0.04 tons CO2
            6 -> 0.08f // Energy saving reduces ~0.08 tons CO2 per month
            7 -> 0.01f // Reusable bottle saves ~0.01 tons CO2
            8 -> 0.03f // Local shopping saves ~0.03 tons CO2
            else -> 0.01f
        }

        // Save the carbon reduction
        val sharedPreferences = requireActivity().getSharedPreferences("CarbonFootprintPrefs", Context.MODE_PRIVATE)
        val currentReduction = sharedPreferences.getFloat("carbon_reduction", 0f)
        val newReduction = currentReduction + carbonReduction

        sharedPreferences.edit().putFloat("carbon_reduction", newReduction).apply()

        // If the user has a calculated carbon footprint, update it
        val currentFootprint = sharedPreferences.getFloat("carbon_footprint", 0f)
        if (currentFootprint > 0) {
            val updatedFootprint = maxOf(0f, currentFootprint - carbonReduction)
            sharedPreferences.edit().putFloat("carbon_footprint", updatedFootprint).apply()
        }
    }

    private fun showLogActivityDialog() {
        val dialog = Dialog(requireContext())
        val dialogBinding = DialogLogActivityBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        // Set up the activity spinner
        val activityNames = sustainableActivities.map { it.name }.toTypedArray()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, activityNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerActivity.adapter = adapter

        // Set up the submit button
        dialogBinding.btnSubmitActivity.setOnClickListener {
            val selectedPosition = dialogBinding.spinnerActivity.selectedItemPosition
            if (selectedPosition >= 0) {
                val selectedActivity = sustainableActivities[selectedPosition]
                val note = dialogBinding.etDescription.text.toString()

                // Log the activity with optional note
                if (note.isNotEmpty()) {
                    // Save note if provided
                    val sharedPreferences = requireActivity().getSharedPreferences("EcoPointsPrefs", Context.MODE_PRIVATE)
                    val notes = sharedPreferences.getStringSet("activity_notes", mutableSetOf()) ?: mutableSetOf()
                    notes.add("${selectedActivity.id}|$note|${System.currentTimeMillis()}")
                    sharedPreferences.edit().putStringSet("activity_notes", notes).apply()
                }

                logActivity(selectedActivity)
                dialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Please select an activity", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = RewardFragment()
    }
}

// Data class for sustainable activities
data class SustainableActivity(
    val id: Int,
    val name: String,
    val description: String,
    val pointsReward: Int,
    val iconResId: Int
)

// Adapter for the RecyclerView
class SustainableActivitiesAdapter(
    private val activities: List<SustainableActivity>,
    private val onLogClicked: (SustainableActivity) -> Unit
) : RecyclerView.Adapter<SustainableActivitiesAdapter.ActivityViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityViewHolder {
        val binding = ActivityItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActivityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActivityViewHolder, position: Int) {
        holder.bind(activities[position])
    }

    override fun getItemCount() = activities.size

    inner class ActivityViewHolder(private val binding: ActivityItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: SustainableActivity) {
            binding.tvActivityName.text = activity.name
            binding.tvActivityDescription.text = activity.description
            binding.tvPointsReward.text = "+${activity.pointsReward}"
            binding.ivActivityIcon.setImageResource(activity.iconResId)

            binding.btnLogActivity.setOnClickListener {
                onLogClicked(activity)
            }
        }
    }
}
