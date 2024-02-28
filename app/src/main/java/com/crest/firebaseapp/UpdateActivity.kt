package com.crest.firebaseapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.practice.firebaseapp.databinding.ActivityUpdateBinding
import com.squareup.picasso.Picasso

class UpdateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUpdateBinding
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")
    private lateinit var startActivityIntent: ActivityResultLauncher<Intent>
    private val firebaseStore: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageReference: StorageReference = firebaseStore.reference
    private var imageUri: Uri? = null
    private var mId = ""
    private var mImageUrl = ""
    private var mImageName = ""
    private var mAge = 0
    private var mName = ""
    private var mEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Update User"

        registerActivityForResult()
        if (intent.hasExtra("age"))
            mAge = intent.getIntExtra("age", 0)

        if (intent.hasExtra("image"))
            mImageUrl = intent.getStringExtra("image").toString()

        if (intent.hasExtra("imageName"))
            mImageName = intent.getStringExtra("imageName").toString()

        if (intent.hasExtra("id"))
            mId = intent.getStringExtra("id").toString()

        if (intent.hasExtra("name"))
            mName = intent.getStringExtra("name").toString()

        if (intent.hasExtra("email"))
            mEmail = intent.getStringExtra("email").toString()

        binding.updateAgeEt.setText(mAge.toString())
        binding.updateNameEt.setText(mName)
        binding.updateEmailEt.setText(mEmail)
        Picasso.get().load(mImageUrl).into(binding.imageView)
        binding.imageView.setOnClickListener {
            chooseImage()
        }
        binding.updateAddUserBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.updateAddUserBtn.isEnabled = false
            if (imageUri != null) {
                uploadPhoto()
            } else {
                addUserToDatabase(mImageUrl)
            }
        }
    }

    private fun addUserToDatabase(url: String) {
        val name: String = binding.updateNameEt.text.toString().trim()
        val email: String = binding.updateEmailEt.text.toString().trim()
        val age: Int = binding.updateAgeEt.text.toString().trim().toInt()
        if (name == mName && email == mEmail && mAge == age)
            finish()
        else {
            val user = Users(mId, name, age, email, url, mImageName)

            myReference.child(mId).setValue(user).addOnSuccessListener {
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

    private fun registerActivityForResult() {
        startActivityIntent = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val resultCode = result.resultCode
            val imageData = result.data

            if (resultCode == RESULT_OK && imageData != null) {
                imageUri = imageData.data
                imageUri?.let {
                    Picasso.get().load(it).into(binding.imageView)
                }
            }
        }
    }

    private fun chooseImage() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityIntent.launch(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityIntent.launch(intent)

        }
    }

    private fun uploadPhoto() {
        val imageReference = storageReference.child("images").child("user photo")
            .child(mImageName)
        imageUri?.let { uri ->
            imageReference.putFile(uri).addOnSuccessListener {
                val uploadedImageRef =
                    storageReference.child("images").child("user photo").child(mImageName)
                uploadedImageRef.downloadUrl.addOnSuccessListener { url ->
                    addUserToDatabase(url.toString())
                }
                    .addOnFailureListener { e ->
                        binding.progressBar.visibility = View.GONE
                        binding.updateAddUserBtn.isEnabled = true
                        Toast.makeText(
                            applicationContext,
                            "Failure: ${e.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }.addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.updateAddUserBtn.isEnabled = true
                Toast.makeText(
                    applicationContext,
                    "Failure: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}