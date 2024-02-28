package com.crest.firebaseapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.practice.firebaseapp.databinding.ActivityUserBinding
import com.squareup.picasso.Picasso


class UserActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")
    private lateinit var startActivityIntent: ActivityResultLauncher<Intent>
    val firebaseStore: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = firebaseStore.reference
    var imageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerActivityForResult()
        supportActionBar?.title = "Add User"
        binding.addUserBtn.setOnClickListener {
            if (imageUri != null) {
                binding.progressBar.visibility = View.VISIBLE
                binding.addUserBtn.isEnabled = false
                uploadPhoto()
            } else {
                Toast.makeText(applicationContext, "choose image", Toast.LENGTH_SHORT).show()
            }
        }
        binding.imageView.setOnClickListener {
            chooseImage()
        }
    }

    private fun addUserToDatabase(url: String, imageName: String) {
        val name: String = binding.nameEt.text.toString().trim()
        val email: String = binding.emailEt.text.toString().trim()
        val age: Int = binding.ageEt.text.toString().trim().toInt()

        val id = myReference.push().key.toString()

        val user = Users(id, name, age, email, url, imageName)

        myReference.child(id).setValue(user).addOnSuccessListener {
            Toast.makeText(
                applicationContext,
                "The new user has been added to database.",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
            .addOnFailureListener {
                binding.progressBar.visibility = View.GONE
                binding.addUserBtn.isEnabled = true
                Toast.makeText(
                    applicationContext,
                    "Failure - ${it.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun registerActivityForResult() {
        startActivityIntent = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
                val resultCode = result.resultCode
                val imageData = result.data

                if (resultCode == RESULT_OK && imageData != null) {
                    imageUri = imageData.data
                    imageUri?.let {
                        Picasso.get().load(it).into(binding.imageView)
                    }
                }
            }
        )
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
        val imageName = System.currentTimeMillis().toString()
        val imageReference = storageReference.child("images").child("user photo")
            .child(imageName)
        imageUri?.let { uri ->
            imageReference.putFile(uri).addOnSuccessListener {
                val uploadedImageRef =
                    storageReference.child("images").child("user photo").child(imageName)
                uploadedImageRef.downloadUrl.addOnSuccessListener { url ->
                    val imageUrl = url.toString()
                    addUserToDatabase(imageUrl, imageName)
                }
                    .addOnFailureListener { e ->
                        binding.progressBar.visibility = View.GONE
                        binding.addUserBtn.isEnabled = true
                        Toast.makeText(
                            applicationContext,
                            "Failure: ${e.localizedMessage}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }.addOnFailureListener { e ->
                binding.progressBar.visibility = View.GONE
                binding.addUserBtn.isEnabled = true
                Toast.makeText(
                    applicationContext,
                    "Failure: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

}