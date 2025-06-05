package dev.redfox.planetpulse.auth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dev.redfox.planetpulse.auth.LoginActivity
import dev.redfox.planetpulse.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var fdatabase: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var selectedImg: Uri
    private lateinit var email:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        fdatabase = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.tvSignIn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)

        }

        binding.btnSignUp.setOnClickListener {
            email = binding.etEmail.text.toString()
            val name = binding.etName.text.toString()
            var pass = binding.etPassword.text.toString()
            var confirmPass = binding.confirmPassEt.text.toString()

            val sharedPref = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
            val editor2 = sharedPref.edit()
            editor2.putBoolean("hasLoggedIn", true)
            editor2.apply()

            val sharedPreference = getSharedPreferences("EMAIL", Context.MODE_PRIVATE)
            val editor = sharedPreference.edit()
            editor.putString("email", email)
            editor.apply()


            if (email.isNotEmpty() && name.isNotEmpty() && confirmPass.isNotEmpty() && pass.isNotEmpty()
            ) {

                firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {

                        database = FirebaseDatabase.getInstance().getReference("users")

                        database.child(EncodeString(email).toString()).child("Name").setValue(name)
                        database.child(EncodeString(email).toString()).child("Email")
                            .setValue(EncodeString(email).toString())

                        database.child(EncodeString(email).toString()).child("Questions").child("q1").setValue(0)
                        database.child(EncodeString(email).toString()).child("Questions").child("q2").setValue(0)
                        database.child(EncodeString(email).toString()).child("Questions").child("q3").setValue(0)
                        database.child(EncodeString(email).toString()).child("Questions").child("q4").setValue(0)
                        database.child(EncodeString(email).toString()).child("Questions").child("q5").setValue(0)



                        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()


                    }.addOnFailureListener {
                    if (it is FirebaseAuthInvalidUserException) {
                        Toast.makeText(
                            this,
                            "Email cannot be used to create account",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (it is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Email Already Exists", Toast.LENGTH_SHORT).show()
                    } else if (pass != confirmPass) {
                        Toast.makeText(this, "Check Password", Toast.LENGTH_SHORT).show()
                    } else if (it is FirebaseAuthWeakPasswordException) {
                        Toast.makeText(this, "Weak Password", Toast.LENGTH_SHORT).show()
                    } else if (it is FirebaseAuthEmailException) {
                        Toast.makeText(this, "Incorrect Email", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                    it.printStackTrace()
                }

            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }



    fun EncodeString(string: String): String? {
        return string.replace(".", ",")
    }

    fun DecodeString(string: String): String? {
        return string.replace(",", ".")
    }
}