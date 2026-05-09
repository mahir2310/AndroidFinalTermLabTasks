package com.example.universitynewsapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.universitynewsapp.repository.PostRepository
import com.example.universitynewsapp.ui.UserAdapter
import kotlinx.coroutines.launch

class UsersActivity : AppCompatActivity() {
    private val repository = PostRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        val toolbar = findViewById<Toolbar>(R.id.toolbarUsers)
        setSupportActionBar(toolbar)

        val recycler = findViewById<RecyclerView>(R.id.usersRecycler)
        recycler.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val res = repository.getAllUsers()
            if (res.isSuccess) {
                val users = res.getOrNull() ?: emptyList()
                val adapter = UserAdapter(users) { user ->
                    val intent = Intent(this@UsersActivity, UserProfileActivity::class.java)
                    intent.putExtra("userId", user.id)
                    startActivity(intent)
                }
                recycler.adapter = adapter
            }
        }
    }
}

