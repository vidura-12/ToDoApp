package com.example.taskapp

import Task
import TaskAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader

class TaskListActivity : AppCompatActivity() {

    private val FILE_NAME = "tasks.txt"
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    private val editTaskResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val updatedTask = result.data?.getParcelableExtra<Task>("updatedTask") ?: return@registerForActivityResult
            updateTask(updatedTask)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        recyclerView = findViewById(R.id.taskRecyclerView)
        val addTaskButton: Button = findViewById(R.id.addTaskButton)
        val stopwatchButton: Button = findViewById(R.id.Stopwatch)

        recyclerView.layoutManager = LinearLayoutManager(this)

        taskAdapter = TaskAdapter(taskList, { position ->
            showDeleteConfirmationDialog(position)
        }, { position ->
            val task = taskList[position]
            val intent = Intent(this, editTaskActivity::class.java).apply {
                putExtra("task", task)
            }
            editTaskResultLauncher.launch(intent)
        })

        recyclerView.adapter = taskAdapter

        updateTaskList()

        addTaskButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        stopwatchButton.setOnClickListener {
            val intent = Intent(this, StopWatch::class.java)
            startActivity(intent)
        }
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Task")
        builder.setMessage("Are you sure you want to delete this task?")
        builder.setPositiveButton("OK") { dialog, _ ->
            deleteTask(position)
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    // Update the task list from the file
    private fun updateTaskList() {
        val file = getFileStreamPath(FILE_NAME)

        // Check if the file exists before attempting to open it
        if (!file.exists()) {
            // If the file doesn't exist, just return
            return
        }

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


    private fun deleteTask(position: Int) {
        val taskToDelete = taskList[position]
        taskList.removeAt(position)

        updateTasksFile()
        NotificationHelper.sendNotificationDelete(this, taskToDelete)
        taskAdapter.notifyItemRemoved(position)
    }

    private fun updateTask(updatedTask: Task) {
        val position = taskList.indexOfFirst { it.id == updatedTask.id }
        if (position != -1) {
            taskList[position] = updatedTask

            updateTasksFile()
            NotificationHelper.sendNotificationEdit(this, updatedTask)
            taskAdapter.notifyItemChanged(position)
        }
    }

    // Centralized method to write the updated task list to the file
    private fun updateTasksFile() {
        openFileOutput(FILE_NAME, MODE_PRIVATE).use { fos ->
            taskList.forEach { task ->
                val taskString = "${task.title} | ${task.description} | ${task.date} | ${task.time}\n"
                fos.write(taskString.toByteArray())
            }
        }
    }
}
