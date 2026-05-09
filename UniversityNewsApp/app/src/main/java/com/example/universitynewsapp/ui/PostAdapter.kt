package com.example.universitynewsapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.universitynewsapp.R
import com.example.universitynewsapp.model.Post

class PostAdapter(private val onClick: (Post) -> Unit) : ListAdapter<Post, PostAdapter.PostViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PostViewHolder(itemView: View, val onClick: (Post) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val avatar: ImageView = itemView.findViewById(R.id.avatar)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val body: TextView = itemView.findViewById(R.id.body)
        private val userBadge: TextView = itemView.findViewById(R.id.userBadge)
        private val postId: TextView = itemView.findViewById(R.id.postId)
        private var currentPost: Post? = null

        init {
            itemView.setOnClickListener {
                currentPost?.let { onClick(it) }
            }
        }

        fun bind(post: Post) {
            currentPost = post
            title.text = post.title
            body.text = post.body
            userBadge.text = "User ${post.userId}"
            postId.text = "#${post.id}"
            // JSONPlaceholder doesn't include avatar URLs; generate placeholder using initials or a colored circle.
            // For simplicity, use Glide to load the launcher round as avatar.
            Glide.with(avatar.context)
                .load(R.mipmap.ic_launcher_round)
                .circleCrop()
                .into(avatar)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }
}

