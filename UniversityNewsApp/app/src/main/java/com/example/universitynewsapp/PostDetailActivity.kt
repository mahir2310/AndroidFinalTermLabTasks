package com.example.universitynewsapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.universitynewsapp.repository.PostRepository
import kotlinx.coroutines.launch

class PostDetailActivity : AppCompatActivity() {
    private val repository = PostRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val postId = intent.getIntExtra("postId", -1)
        val postTitle = findViewById<TextView>(R.id.postTitle)
        val postBody = findViewById<TextView>(R.id.postBody)
        val authorCard = findViewById<CardView>(R.id.authorCard)
        val authorName = findViewById<TextView>(R.id.authorName)
        val authorEmail = findViewById<TextView>(R.id.authorEmail)
        val authorCompany = findViewById<TextView>(R.id.authorCompany)
        val commentsProgress = findViewById<ProgressBar>(R.id.commentsProgress)
        val commentsContainer = findViewById<android.widget.LinearLayout>(R.id.commentsContainer)

        // Show loading for post
        postTitle.text = ""
        postBody.text = "Loading..."

        lifecycleScope.launch {
            val postResult = repository.getPostById(postId)
            if (postResult.isSuccess) {
                val post = postResult.getOrNull()!!
                postTitle.text = post.title
                postBody.text = post.body

                // load author
                val userResult = repository.getUserById(post.userId)
                if (userResult.isSuccess) {
                    val user = userResult.getOrNull()!!
                    authorName.text = user.name
                    authorEmail.text = user.email
                    authorCompany.text = "${user.company.name} — ${user.company.catchPhrase}"
                    authorCard.setOnClickListener {
                        val intent = Intent(this@PostDetailActivity, UserProfileActivity::class.java)
                        intent.putExtra("userId", user.id)
                        startActivity(intent)
                    }
                } else {
                    authorName.text = "Author info unavailable"
                }

                // load comments
                commentsProgress.visibility = View.VISIBLE
                val commentsResult = repository.getCommentsByPost(post.id)
                commentsProgress.visibility = View.GONE
                if (commentsResult.isSuccess) {
                    val comments = commentsResult.getOrNull() ?: emptyList()
                    // render comments in container
                    for (c in comments) {
                        val view = layoutInflater.inflate(R.layout.item_comment, commentsContainer, false)
                        val name = view.findViewById<TextView>(R.id.commentName)
                        val email = view.findViewById<TextView>(R.id.commentEmail)
                        val body = view.findViewById<TextView>(R.id.commentBody)
                        name.text = c.name
                        email.text = c.email
                        body.text = c.body
                        commentsContainer.addView(view)
                    }
                } else {
                    val tv = TextView(this@PostDetailActivity)
                    tv.text = "Failed to load comments"
                    commentsContainer.addView(tv)
                }

            } else {
                postTitle.text = "Failed to load post"
                postBody.text = postResult.exceptionOrNull()?.message ?: ""
            }
        }
    }
}
