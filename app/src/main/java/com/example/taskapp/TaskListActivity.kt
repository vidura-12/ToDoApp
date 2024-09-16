package com.example.taskapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.InputStreamReader

class TaskListActivity : AppCompatActivity() {

    private val FILE_NAME = "tasks.txt"
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        recyclerView = findViewById(R.id.taskRecyclerView)
        val addTaskButton: Button = findViewById(R.id.addTaskButton)

        // Set the GridLayoutManager with 2 columns
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        taskAdapter = TaskAdapter(taskList)
        recyclerView.adapter = taskAdapter

        updateTaskList()

        addTaskButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun updateTaskList() {
        taskList.clear()
        openFileInput(FILE_NAME).use { fis ->
            BufferedReader(InputStreamReader(fis)).use { reader ->
                reader.forEachLine { line ->
                    val taskDetails = line.split(" | ")
                    if (taskDetails.size == 4) {
                        val task = Task(taskDetails[0], taskDetails[1], taskDetails[2], taskDetails[3])
                        taskList.add(task)
                    }
                }
            }
        }
        taskAdapter.notifyDataSetChanged()
    }
}
