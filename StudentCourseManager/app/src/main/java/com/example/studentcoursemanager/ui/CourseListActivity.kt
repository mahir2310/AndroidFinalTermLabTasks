package com.example.studentcoursemanager.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studentcoursemanager.R
import com.example.studentcoursemanager.model.Course
import com.example.studentcoursemanager.repo.FirebaseCourseRepository
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView

class CourseListActivity : AppCompatActivity() {

    private val repo = FirebaseCourseRepository()
    private lateinit var adapter: CourseAdapter
    private var listenerToken: Any? = null
    private var allCourses = listOf<Course>()

    // views
    private lateinit var toolbar: MaterialToolbar
    private lateinit var rvCourses: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_list)
        toolbar = findViewById(R.id.toolbar)
        rvCourses = findViewById(R.id.rvCourses)
        fabAdd = findViewById(R.id.fabAdd)
        tvEmpty = findViewById(R.id.tvEmpty)

        setSupportActionBar(toolbar)

        adapter = CourseAdapter(mutableListOf(), onEdit = { course ->
            val i = Intent(this, CourseDetailActivity::class.java)
            i.putExtra("course", course)
            i.putExtra("mode", "edit")
            startActivity(i)
        }, onDelete = { course ->
            repo.deleteCourse(course.id) { result ->
                runOnUiThread {
                    result.onSuccess { Toast.makeText(this, "Course deleted", Toast.LENGTH_SHORT).show() }
                    result.onFailure { e -> Toast.makeText(this, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show() }
                }
            }
        }, onOpen = { course ->
            val i = Intent(this, CourseDetailActivity::class.java)
            i.putExtra("course", course)
            i.putExtra("mode", "view")
            startActivity(i)
        })

        rvCourses.layoutManager = LinearLayoutManager(this)
        rvCourses.adapter = adapter

        fabAdd.setOnClickListener {
            startActivity(Intent(this, CourseDetailActivity::class.java).apply { putExtra("mode", "add") })
        }

        listenerToken = repo.listenCourses { result ->
            runOnUiThread {
                result.onSuccess { list ->
                    allCourses = list.sortedBy { it.name }
                    adapter.setItems(allCourses)
                    tvEmpty.visibility = if (allCourses.isEmpty()) View.VISIBLE else View.GONE
                }
                result.onFailure { e -> Toast.makeText(this, "Failed to load: ${e.message}", Toast.LENGTH_SHORT).show() }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerToken?.let { repo.stopListening(it) }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView
        searchView?.queryHint = "Search courses"
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                val q = newText?.trim() ?: ""
                if (q.isEmpty()) adapter.setItems(allCourses)
                else adapter.setItems(allCourses.filter { it.name.contains(q, true) || it.code.contains(q, true) })
                return true
            }
        })
        return true
    }
}
