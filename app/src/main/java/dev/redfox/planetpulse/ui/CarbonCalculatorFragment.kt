package dev.redfox.planetpulse.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dev.redfox.planetpulse.R
import dev.redfox.planetpulse.databinding.FragmentCarbonCalculatorBinding

class CarbonCalculatorFragment : Fragment() {

    private var _binding: FragmentCarbonCalculatorBinding? = null
    private val binding get() = _binding!!
    private val TAG = "CarbonCalculatorFrag"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarbonCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load any previously saved values
        loadSavedValues()

        // Set up click listener for the calculate button
        binding.btnCalculate.setOnClickListener {
            calculateCarbonFootprint()
        }
    }

    private fun loadSavedValues() {
        val sharedPreferences = requireActivity().getSharedPreferences("CarbonFootprintPrefs", Context.MODE_PRIVATE)

        // Load previous input values if they exist
        binding.etCarKm.setText(sharedPreferences.getFloat("car_km", 0f).toString())
        binding.etPublicTransportKm.setText(sharedPreferences.getFloat("public_transport_km", 0f).toString())
        binding.etFlights.setText(sharedPreferences.getFloat("flights", 0f).toString())
        binding.etElectricity.setText(sharedPreferences.getFloat("electricity", 0f).toString())
        binding.etGas.setText(sharedPreferences.getFloat("gas", 0f).toString())

        // Set diet radio button
        val dietType = sharedPreferences.getInt("diet_type", -1)
        if (dietType != -1) {
            when (dietType) {
                0 -> binding.rbHighMeat.isChecked = true
                1 -> binding.rbMediumMeat.isChecked = true
                2 -> binding.rbLowMeat.isChecked = true
                3 -> binding.rbVegetarian.isChecked = true
                4 -> binding.rbVegan.isChecked = true
            }
        }
    }

    private fun calculateCarbonFootprint() {
        try {
            // Get input values
            val transportationEmissions = calculateTransportationEmissions()
            val homeEmissions = calculateHomeEmissions()
            val dietEmissions = calculateDietEmissions()

            // Calculate total carbon footprint
            val totalEmissions = transportationEmissions + homeEmissions + dietEmissions

            // Save all input values and the result
            saveInputValues(totalEmissions)

            // Save component values for the dashboard graph
            saveComponentValues(transportationEmissions, homeEmissions, dietEmissions)

            // Display result
            binding.tvResult.text = String.format("%.2f", totalEmissions)
            binding.resultLayout.visibility = View.VISIBLE

            Log.d(TAG, "Carbon footprint calculated: $totalEmissions tonnes CO2/year")

        } catch (e: Exception) {
            Log.e(TAG, "Error calculating carbon footprint", e)
            Toast.makeText(context, "Please fill in all fields correctly", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveInputValues(totalEmissions: Float) {
        val sharedPreferences = requireActivity().getSharedPreferences("CarbonFootprintPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Save input values
        editor.putFloat("car_km", binding.etCarKm.text.toString().toFloatOrNull() ?: 0f)
        editor.putFloat("public_transport_km", binding.etPublicTransportKm.text.toString().toFloatOrNull() ?: 0f)
        editor.putFloat("flights", binding.etFlights.text.toString().toFloatOrNull() ?: 0f)
        editor.putFloat("electricity", binding.etElectricity.text.toString().toFloatOrNull() ?: 0f)
        editor.putFloat("gas", binding.etGas.text.toString().toFloatOrNull() ?: 0f)

        // Save diet type
        val dietType = when(binding.rgDiet.checkedRadioButtonId) {
            R.id.rbHighMeat -> 0
            R.id.rbMediumMeat -> 1
            R.id.rbLowMeat -> 2
            R.id.rbVegetarian -> 3
            R.id.rbVegan -> 4
            else -> -1
        }
        editor.putInt("diet_type", dietType)

        // Save the calculated carbon footprint
        editor.putFloat("carbon_footprint", totalEmissions)

        // Save calculation timestamp
        editor.putLong("calculation_timestamp", System.currentTimeMillis())

        editor.apply()

        Log.d(TAG, "Saved carbon footprint: $totalEmissions tonnes CO2/year")
    }

    private fun saveComponentValues(transportation: Float, home: Float, diet: Float) {
        val sharedPreferences = requireActivity().getSharedPreferences("CarbonFootprintPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Calculate component values based on our calculation
        // For this example, we'll split transportation into flying and mobility
        val flying = transportation * 0.6f  // 60% of transportation is from flying
        val mobility = transportation * 0.4f // 40% of transportation is from other mobility

        // Home energy is housing
        val housing = home

        // Diet remains as calculated

        // Spending is a small additional component
        val spending = (transportation + home + diet) * 0.05f // 5% of total

        // Save all components
        editor.putFloat("flying", flying)
        editor.putFloat("mobility", mobility)
        editor.putFloat("housing", housing)
        editor.putFloat("diet", diet)
        editor.putFloat("spending", spending)

        editor.apply()
    }

    private fun calculateTransportationEmissions(): Float {
        val carKm = binding.etCarKm.text.toString().toFloatOrNull() ?: 0f
        val publicTransportKm = binding.etPublicTransportKm.text.toString().toFloatOrNull() ?: 0f
        val flightsPerYear = binding.etFlights.text.toString().toFloatOrNull() ?: 0f

        // Weekly car km to yearly
        val yearlyCarKm = carKm * 52

        // Weekly public transport km to yearly
        val yearlyPublicTransportKm = publicTransportKm * 52

        // Approximate emission factors (kg CO2 per km)
        val carEmissionFactor = 0.12f
        val publicTransportEmissionFactor = 0.04f
        val flightEmissionFactor = 250f // per flight (average)

        val emissions = (yearlyCarKm * carEmissionFactor) +
                (yearlyPublicTransportKm * publicTransportEmissionFactor) +
                (flightsPerYear * flightEmissionFactor)

        // Convert from kg to tonnes
        return emissions / 1000
    }

    private fun calculateHomeEmissions(): Float {
        val electricityKwh = binding.etElectricity.text.toString().toFloatOrNull() ?: 0f
        val gasUsage = binding.etGas.text.toString().toFloatOrNull() ?: 0f

        // Monthly to yearly
        val yearlyElectricity = electricityKwh * 12
        val yearlyGas = gasUsage * 12

        // Approximate emission factors
        val electricityEmissionFactor = 0.5f // kg CO2 per kWh
        val gasEmissionFactor = 0.2f // kg CO2 per unit

        val emissions = (yearlyElectricity * electricityEmissionFactor) +
                (yearlyGas * gasEmissionFactor)

        // Convert from kg to tonnes
        return emissions / 1000
    }

    private fun calculateDietEmissions(): Float {
        val dailyEmissions = when(binding.rgDiet.checkedRadioButtonId) {
            R.id.rbHighMeat -> 3.3f
            R.id.rbMediumMeat -> 2.5f
            R.id.rbLowMeat -> 1.9f
            R.id.rbVegetarian -> 1.7f
            R.id.rbVegan -> 1.5f
            else -> 2.5f // Default to medium
        }

        // Daily emissions in kg CO2 based on diet type
        // Multiply by 365 for annual footprint and convert to tonnes
        return (dailyEmissions * 365) / 1000
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
