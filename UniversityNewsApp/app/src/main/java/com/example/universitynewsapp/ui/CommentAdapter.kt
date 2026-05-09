package com.example.universitynewsapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.universitynewsapp.R
import com.example.universitynewsapp.model.Comment

class CommentAdapter(private val items: List<Comment>) : RecyclerView.Adapter<CommentAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.commentName)
        private val email: TextView = itemView.findViewById(R.id.commentEmail)
        private val body: TextView = itemView.findViewById(R.id.commentBody)

        fun bind(c: Comment) {
            name.text = c.name
            email.text = c.email
            body.text = c.body
        }
    }
}

