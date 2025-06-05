package dev.redfox.planetpulse.auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

import dev.redfox.planetpulse.QuestionsActivity
import dev.redfox.planetpulse.R
import dev.redfox.planetpulse.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {


        lateinit var binding: ActivityLoginBinding
        lateinit var firebaseAuth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)
            FirebaseApp.initializeApp(this)
            window.setStatusBarColor(ContextCompat.getColor(baseContext, R.color.yellow))
            firebaseAuth = FirebaseAuth.getInstance()
            binding.tvSignUp.setOnClickListener {
                val intent = Intent(this, SignUpActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }

            binding.btnSignIn.setOnClickListener {
                val email = binding.etEmail.text.toString()
                val pass = binding.etPassword.text.toString()

                if (email.isNotEmpty() && pass.isNotEmpty()) {

                    firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {

                            val sharedPreference =
                                getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
                            val editor = sharedPreference.edit()
                            editor.putBoolean("hasLoggedIn", true)
                            editor.apply()

                            val sharedPref = getSharedPreferences("EMAIL", Context.MODE_PRIVATE)
                            val editor2 = sharedPref.edit()
                            editor2.putString("email", email)
                            editor2.apply()

                            val intent = Intent(this, QuestionsActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT)
                        .show()

                }
            }
        }

    }
