package com.example.universitynewsapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.universitynewsapp.model.Post
import com.example.universitynewsapp.repository.PostRepository
import com.example.universitynewsapp.ui.PostAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val repository = PostRepository()
    private lateinit var adapter: PostAdapter
    private var allPosts: List<Post> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PostAdapter { post ->
            val intent = Intent(this, PostDetailActivity::class.java)
            intent.putExtra("postId", post.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val errorLayout = findViewById<View>(R.id.errorLayout)
        val errorText = findViewById<TextView>(R.id.errorText)
        val retryButton = findViewById<Button>(R.id.retryButton)
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)

        fun showLoading() {
            progressBar.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            errorLayout.visibility = View.GONE
        }
        fun showSuccess(posts: List<Post>) {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            errorLayout.visibility = View.GONE
            allPosts = posts
            adapter.submitList(posts)
        }
        fun showError(message: String) {
            progressBar.visibility = View.GONE
            recyclerView.visibility = View.GONE
            errorLayout.visibility = View.VISIBLE
            errorText.text = message
        }

        retryButton.setOnClickListener { loadPosts() }
        swipeRefresh.setOnRefreshListener { loadPosts() }

        loadPosts()

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_posts -> {
                    // stay
                    true
                }
                R.id.nav_users -> {
                    val intent = Intent(this, UsersActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun loadPosts() {
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val swipeRefresh = findViewById<SwipeRefreshLayout>(R.id.swipeRefresh)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val errorLayout = findViewById<View>(R.id.errorLayout)
        val errorText = findViewById<TextView>(R.id.errorText)

        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        errorLayout.visibility = View.GONE

        lifecycleScope.launch {
            val result = repository.getAllPosts()
            progressBar.visibility = View.GONE
            swipeRefresh.isRefreshing = false
            if (result.isSuccess) {
                val posts = result.getOrNull() ?: emptyList()
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(posts)
                // store for search
                allPosts = posts
            } else {
                errorLayout.visibility = View.VISIBLE
                errorText.text = when (val ex = result.exceptionOrNull()) {
                    is retrofit2.HttpException -> "Server error: ${ex.code()}"
                    is java.io.IOException -> "Network error. Check your connection."
                    else -> "Something went wrong: ${ex?.message}"
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = "Search posts by title"
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean { return false }
            override fun onQueryTextChange(newText: String?): Boolean {
                filterPosts(newText ?: "")
                return true
            }
        })
        return true
    }

    private fun filterPosts(query: String) {
        if (query.isBlank()) {
            adapter.submitList(allPosts)
            return
        }
        val filtered = allPosts.filter { it.title.contains(query, ignoreCase = true) }
        adapter.submitList(filtered)
    }
}