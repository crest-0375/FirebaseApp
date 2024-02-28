package com.crest.firebaseapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.practice.firebaseapp.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Add User"
        binding.addUserBtn.setOnClickListener {
            addUserToDatabase()
        }
    }

    private fun addUserToDatabase() {
        val name: String = binding.nameEt.text.toString().trim()
        val email: String = binding.emailEt.text.toString().trim()
        val age: Int = binding.ageEt.text.toString().trim().toInt()

        val id = myReference.push().key.toString()

        val user = Users(id, name, age, email)

        myReference.child(id).setValue(user).addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "The new user has been added to database.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
            .addOnFailureListener {
                Toast.makeText(
                    applicationContext,
                    "Failure - ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

}