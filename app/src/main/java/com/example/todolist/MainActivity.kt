package com.example.todolist

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.databinding.ActivityMainBinding

data class Task(var id: Int, var text: String)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ArrayAdapter<String>
    private var tasks: MutableList<Task> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Adapter with custom layout
        adapter = ArrayAdapter(
            this,
            R.layout.list_item_task,
            R.id.taskText,
            tasks.map { "${it.id}: ${it.text}" }
        )
        binding.listView.adapter = adapter

        // Add Task Button
        binding.btnAdd.setOnClickListener {
            val text = binding.editTask.text.toString()
            if (text.isNotEmpty()) {
                val task = Task(id = tasks.size + 1, text = text)
                tasks.add(task)
                refreshList()
                binding.editTask.text.clear()
            }
        }

        // Edit on item click
        binding.listView.setOnItemClickListener { _, _, position, _ ->
            showEditDialog(tasks[position])
        }

        // Search
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                val filtered = if (!newText.isNullOrEmpty()) {
                    tasks.filter { it.text.contains(newText, ignoreCase = true) }
                } else tasks
                adapter.clear()
                adapter.addAll(filtered.map { "${it.id}: ${it.text}" })
                return true
            }
        })
    }

    private fun showEditDialog(task: Task) {
        val input = EditText(this)
        input.setText(task.text)

        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                task.text = input.text.toString()
                refreshList()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun refreshList() {
        adapter.clear()
        adapter.addAll(tasks.map { "${it.id}: ${it.text}" })
        adapter.notifyDataSetChanged()
    }
}
