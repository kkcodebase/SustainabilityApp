package dev.redfox.planetpulse.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import dev.redfox.planetpulse.R
import androidx.fragment.app.Fragment
import dev.redfox.planetpulse.databinding.FragmentGptBinding
import dev.redfox.planetpulse.utils.Constants.Companion.API_KEY
import dev.redfox.planetpulse.utils.Constants.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class GptFragment : Fragment() {

    private var _binding: FragmentGptBinding? = null
    private val binding get() = _binding!!

    val client = OkHttpClient()

    // List of quiz questions, options, and correct answers
    private val questions = listOf(
        Question("What is the process of turning waste materials into new products?", listOf("Recycling", "Composting", "Incineration", "Landfilling"), 0),
        Question("Which of the following is a renewable source of energy?", listOf("Coal", "Solar", "Natural Gas", "Oil"), 1),
        Question("What is the term for reducing the amount of waste produced?", listOf("Reuse", "Recycle", "Reduce", "Replace"), 2),
        Question("Which of these materials can be recycled?", listOf("Plastic Bottles", "Styrofoam", "Chip Bags", "Candy Wrappers"), 0),
        Question("What is the main greenhouse gas responsible for climate change?", listOf("Oxygen", "Carbon Dioxide", "Nitrogen", "Methane"), 1),
        Question("What does the term 'carbon footprint' refer to?", listOf("The amount of waste produced", "The total amount of CO2 emissions from human activities", "The area of forest needed for carbon offset", "The size of a tree's root system"), 1),
        Question("What does biodegradable mean?", listOf("Can be recycled", "Can be broken down naturally by microorganisms", "Can be reused", "Can be incinerated"), 1),
        Question("Which of these is the best way to conserve water?", listOf("Taking shorter showers", "Washing clothes in hot water", "Leaving the tap running while brushing teeth", "Using disposable plastic bottles"), 0)
    )

    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGptBinding.inflate(inflater, container, false)

        // Start video
        startBackgroundVideo()

        // Set the first question
        showNextQuestion()

        binding.btnSend.setOnClickListener {
            // Check if an option is selected
            val selectedOption = binding.radioGroup.checkedRadioButtonId
            if (selectedOption == -1) {
                Toast.makeText(requireContext(), "Please select an answer", Toast.LENGTH_SHORT).show()
            } else {
                val selectedRadioButton = binding.root.findViewById<View>(selectedOption) as RadioButton
                val userAnswer = selectedRadioButton.text.toString()
                checkAnswer(userAnswer)
            }
        }

        return binding.root
    }

    private fun startBackgroundVideo() {
        val uri: Uri = Uri.parse("android.resource://" + "dev.redfox.planetpulse" + "/" + R.raw.gptvideo)
        binding.videoView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        binding.videoView.setVideoURI(uri)
        binding.videoView.start()
        binding.videoView.setOnPreparedListener { it.isLooping = true }
    }

    private fun showNextQuestion() {
        if (currentQuestionIndex < questions.size) {
            val question = questions[currentQuestionIndex]
            binding.tvResponse.text = question.question

            // Remove any previous RadioButtons
            binding.radioGroup.removeAllViews()

            // Add RadioButtons for options
            question.options.forEachIndexed { index, option ->
                val radioButton = RadioButton(requireContext())
                radioButton.text = option
                radioButton.id = View.generateViewId()
                binding.radioGroup.addView(radioButton)
            }

            // Clear any previous selection
            binding.radioGroup.clearCheck()
        } else {
            Toast.makeText(requireContext(), "Quiz Complete! You scored $score out of ${questions.size}", Toast.LENGTH_LONG).show()
            resetQuiz()
        }
    }

    private fun checkAnswer(userAnswer: String) {
        val correctAnswer = questions[currentQuestionIndex].options[questions[currentQuestionIndex].correctAnswerIndex]

        if (userAnswer.equals(correctAnswer, ignoreCase = true)) {
            score++
        }

        // Move to next question
        currentQuestionIndex++
        showNextQuestion()
    }

    private fun resetQuiz() {
        // Reset quiz to start again
        currentQuestionIndex = 0
        score = 0
    }

    data class Question(val question: String, val options: List<String>, val correctAnswerIndex: Int)
}
