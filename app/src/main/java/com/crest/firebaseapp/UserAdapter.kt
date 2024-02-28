package com.crest.firebaseapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practice.firebaseapp.databinding.ItemUserBinding
import com.squareup.picasso.Picasso

class UserAdapter(private val context: Context, private val list: ArrayList<Users>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: Users) {
            Picasso.get().load(user.imageUrl).into(binding.imageView)
            binding.textView.text = user.userName
            binding.textView2.text = user.userAge.toString()
            binding.textView3.text = user.userEmail
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUserBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(context, UpdateActivity::class.java)
            intent.putExtra("id", list[position].userId)
            intent.putExtra("name", list[position].userName)
            intent.putExtra("age", list[position].userAge)
            intent.putExtra("email", list[position].userEmail)
            intent.putExtra("image", list[position].imageUrl)
            intent.putExtra("imageName", list[position].imageName)
            context.startActivity(intent)
        }
    }

    fun getUserId(position: Int): String {
        return list[position].userId
    }

    fun getImageName(position: Int): String {
        return list[position].imageName
    }
}