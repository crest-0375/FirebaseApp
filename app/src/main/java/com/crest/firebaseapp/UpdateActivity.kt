package com.crest.firebaseapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.practice.firebaseapp.databinding.ActivityUpdateBinding

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Update User"
        var id = ""
        var age = 0
        var name = ""
        var email = ""

        if (intent.hasExtra("age"))
            age = intent.getIntExtra("age", 0)

        if (intent.hasExtra("id"))
            id = intent.getStringExtra("id").toString()

        if (intent.hasExtra("name"))
            name = intent.getStringExtra("name").toString()

        if (intent.hasExtra("email"))
            email = intent.getStringExtra("email").toString()

        binding.updateAgeEt.setText(age.toString())
        binding.updateNameEt.setText(name)
        binding.updateEmailEt.setText(email)

        binding.updateAddUserBtn.setOnClickListener {
            addUserToDatabase(id)
        }
    }

    private fun addUserToDatabase(id:String) {
        val name: String = binding.updateNameEt.text.toString().trim()
        val email: String = binding.updateEmailEt.text.toString().trim()
        val age: Int = binding.updateAgeEt.text.toString().trim().toInt()


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