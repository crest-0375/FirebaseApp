package com.crest.firebaseapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.practice.firebaseapp.databinding.ActivityForgetBinding

class ForgetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityForgetBinding
    val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fotgetBtn.setOnClickListener {
            val email = binding.forgetEtEmail.text.toString().trim()
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                Toast.makeText(
                    applicationContext,
                    "We sent you link on your mail address",
                    Toast.LENGTH_SHORT
                ).show()
                finish()

            }
                .addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "Failure: ${it.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }
}