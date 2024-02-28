package com.crest.firebaseapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.practice.firebaseapp.databinding.ActivityNumberBinding
import java.util.concurrent.TimeUnit

class NumberActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNumberBinding
    private val auth = FirebaseAuth.getInstance()
    private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    var verificationCode = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNumberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendCodeBtn.setOnClickListener {
            val mobile = binding.numberEt.text.toString().trim()
            val options = PhoneAuthOptions.newBuilder(auth).setPhoneNumber(mobile)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this@NumberActivity)
                .setCallbacks(mCallbacks)
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
        binding.verifyCodeBtn.setOnClickListener {
            signInWithSMS()
        }
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                Toast.makeText(this@NumberActivity, "Success!", Toast.LENGTH_SHORT).show()

            }

            override fun onVerificationFailed(p0: FirebaseException) {
                p0.localizedMessage?.let { Log.d("TAG", it) }
                Toast.makeText(
                    this@NumberActivity,
                    "Failure: ${p0.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()

            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                verificationCode = p0
            }
        }
    }

    private fun signInWithSMS() {
        val userEnteredCode = binding.varificationCodeEt.text.toString().trim()
        val credential = PhoneAuthProvider.getCredential(verificationCode, userEnteredCode)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnSuccessListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }.addOnFailureListener {
            Toast.makeText(this, "Failure: ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}