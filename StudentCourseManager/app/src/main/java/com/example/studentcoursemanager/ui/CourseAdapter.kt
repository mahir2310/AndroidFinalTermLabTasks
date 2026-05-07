package com.example.studentcoursemanager.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcoursemanager.R
import com.example.studentcoursemanager.model.Course

class CourseAdapter(
    private var items: MutableList<Course> = mutableListOf(),
    private val onEdit: (Course) -> Unit,
    private val onDelete: (Course) -> Unit,
    private val onOpen: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.VH>() {

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCourseName)
        val tvCode: TextView = view.findViewById(R.id.tvCourseCode)
        val tvInstructor: TextView = view.findViewById(R.id.tvInstructor)
        val tvCredits: TextView = view.findViewById(R.id.tvCredits)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_course, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = items[position]
        holder.tvName.text = c.name
        holder.tvCode.text = c.code
        holder.tvInstructor.text = c.instructor
        holder.tvCredits.text = "Credits: ${c.credits}"
        holder.btnEdit.setOnClickListener { onEdit(c) }
        holder.btnDelete.setOnClickListener { onDelete(c) }
        holder.itemView.setOnClickListener { onOpen(c) }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<Course>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addItem(course: Course) {
        items.add(course)
        notifyItemInserted(items.size - 1)
    }

    fun removeItemById(id: String) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0) {
            items.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }
}

