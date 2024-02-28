package com.crest.firebaseapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.practice.firebaseapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.signupBtn.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        binding.signinBtn.setOnClickListener {
            val email = binding.loginEtEmail.text.toString().trim()
            val password = binding.loginEtPass.text.toString().trim()
            signInToFireBase(email, password)
        }
        binding.forgotBtn.setOnClickListener {
            startActivity(Intent(this, ForgetActivity::class.java))
        }
        binding.signInWithNumber.setOnClickListener {
            startActivity(Intent(this, NumberActivity::class.java))
            finish()
        }
    }

    override fun onStart() {

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        super.onStart()
    }

    private fun signInToFireBase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(this, "Failure : ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
    }
}