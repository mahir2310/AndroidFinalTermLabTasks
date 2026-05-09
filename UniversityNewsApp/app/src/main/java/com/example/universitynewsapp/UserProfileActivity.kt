package com.example.universitynewsapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universitynewsapp.repository.PostRepository
import com.example.universitynewsapp.ui.PostAdapter
import kotlinx.coroutines.launch

class UserProfileActivity : AppCompatActivity() {
    private val repository = PostRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val avatarBig = findViewById<TextView>(R.id.avatarBig)
        val fullName = findViewById<TextView>(R.id.fullName)
        val handle = findViewById<TextView>(R.id.handle)
        val email = findViewById<TextView>(R.id.email)
        val phone = findViewById<TextView>(R.id.phone)
        val website = findViewById<TextView>(R.id.website)
        val company = findViewById<TextView>(R.id.company)
        val postsRecycler = findViewById<RecyclerView>(R.id.userPostsRecycler)
        postsRecycler.layoutManager = LinearLayoutManager(this)

        val userId = intent.getIntExtra("userId", -1)

        lifecycleScope.launch {
            val userRes = repository.getUserById(userId)
            if (userRes.isSuccess) {
                val user = userRes.getOrNull()!!
                fullName.text = user.name
                handle.text = "@${user.username}"
                email.text = user.email
                phone.text = user.phone
                website.text = user.website
                company.text = "${user.company.name} — ${user.company.catchPhrase}"

                // initials
                val parts = user.name.split(" ")
                val initials = when {
                    parts.size >= 2 -> "${parts[0][0]}${parts[1][0]}"
                    parts.isNotEmpty() -> "${parts[0][0]}"
                    else -> "?"
                }
                avatarBig.text = initials

                val postsRes = repository.getPostsByUser(user.id)
                if (postsRes.isSuccess) {
                    val posts = postsRes.getOrNull() ?: emptyList()
                    val adapter = PostAdapter { post ->
                        val intent = Intent(this@UserProfileActivity, PostDetailActivity::class.java)
                        intent.putExtra("postId", post.id)
                        startActivity(intent)
                    }
                    postsRecycler.adapter = adapter
                    adapter.submitList(posts)
                }
            }
        }
    }
}

