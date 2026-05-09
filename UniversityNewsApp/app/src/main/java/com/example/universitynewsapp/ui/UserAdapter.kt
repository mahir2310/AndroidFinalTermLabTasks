package com.example.universitynewsapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.universitynewsapp.R
import com.example.universitynewsapp.model.User

class UserAdapter(private val items: List<User>, private val onClick: (User) -> Unit) : RecyclerView.Adapter<UserAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return VH(v, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class VH(itemView: View, val onClick: (User) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val avatarCircle: TextView = itemView.findViewById(R.id.avatarCircle)
        private val name: TextView = itemView.findViewById(R.id.name)
        private val username: TextView = itemView.findViewById(R.id.username)
        private val email: TextView = itemView.findViewById(R.id.email)
        private var current: User? = null

        init {
            itemView.setOnClickListener { current?.let { onClick(it) } }
        }

        fun bind(u: User) {
            current = u
            name.text = u.name
            username.text = "@${u.username}"
            email.text = u.email
            // initials
            val parts = u.name.split(" ")
            val initials = when {
                parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}"
                parts.isNotEmpty() -> "${parts[0][0]}"
                else -> "?"
            }
            avatarCircle.text = initials
        }
    }
}

