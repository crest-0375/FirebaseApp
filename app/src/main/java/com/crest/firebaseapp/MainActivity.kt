package com.crest.firebaseapp

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.practice.firebaseapp.R
import com.practice.firebaseapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var adapter: UserAdapter
    val firebaseStore: FirebaseStorage = FirebaseStorage.getInstance()
    val storageReference: StorageReference = firebaseStore.reference
    val auth = FirebaseAuth.getInstance()
    private val myReference: DatabaseReference = database.reference.child("Users")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this, UserActivity::class.java))
        }
        supportActionBar?.title = "Users Database"
        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val id = adapter.getUserId(viewHolder.adapterPosition)
                val imageName = adapter.getImageName(viewHolder.adapterPosition)
                storageReference.child("images").child("user photo").child(imageName).delete().addOnSuccessListener {
                    myReference.child(id).removeValue().addOnSuccessListener {
                        Toast.makeText(applicationContext, "User deleted!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

        }).attachToRecyclerView(binding.recyclerView)
        retrieveDataFromDatabase()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.delete_all_user) {
            AlertDialog.Builder(this).setTitle("Delete all Users?")
                .setMessage("Are you sure you want to delete all the users? you can delete one user by swiping left and right.")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, _ ->
                    deleteAllUsers()
                    dialog.dismiss()
                })
                .setNegativeButton("No", null)
                .create().show()

        }
        if (item.itemId == R.id.sign_out) {
            AlertDialog.Builder(this).setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, _ ->
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                    dialog.dismiss()
                })
                .setNegativeButton("No", null)
                .create().show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllUsers() {
        storageReference.child("images").delete().addOnSuccessListener {
            myReference.removeValue().addOnCompleteListener {
                Toast.makeText(this, "All users deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun retrieveDataFromDatabase() {
        myReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = arrayListOf<Users>()
                for (eachUser in snapshot.children) {
                    val user = eachUser.getValue(Users::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }
                adapter = UserAdapter(this@MainActivity, userList)
                binding.recyclerView.adapter = adapter
                binding.recyclerView.setHasFixedSize(true)
                binding.recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}