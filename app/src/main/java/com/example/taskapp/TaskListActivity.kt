package com.example.taskapp

import Task
import TaskAdapter
import android.app.Activity
import android.content.DialogInterface
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
            val position = taskList.indexOfFirst { it.id == updatedTask.id }
            if (position != -1) {
                taskList[position] = updatedTask
                updateTaskList()  // Refresh the task list
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        recyclerView = findViewById(R.id.taskRecyclerView)
        val addTaskButton: Button = findViewById(R.id.addTaskButton)
        val Stopwatch: Button = findViewById(R.id.Stopwatch)
        // Set the LinearLayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter with the task list and click listeners
        taskAdapter = TaskAdapter(taskList, { position ->
            // Show confirmation dialog before deleting the task
            showDeleteConfirmationDialog(position)
        }, { position ->
            // Navigate to EditTaskActivity on single tap
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
        Stopwatch.setOnClickListener {
            val intent = Intent(this, StopWatch::class.java)
            startActivity(intent)
        }
    }

    // Show a confirmation dialog for deleting a task
    private fun showDeleteConfirmationDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Task")
        builder.setMessage("Are you sure you want to delete this task?")

        // Handle the positive "OK" button action
        builder.setPositiveButton("OK") { dialog, _ ->
            deleteTask(position)  // Delete the task if OK is clicked
            dialog.dismiss()
        }

        // Handle the negative "Cancel" button action
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()  // Close the dialog without any action
        }

        // Show the dialog
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    // Update the task list from the file
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

    // Delete a task by position and update the file
    private fun deleteTask(position: Int) {
        // Get the task to delete
        val taskToDelete = taskList[position]

        // Remove the task from the list
        taskList.removeAt(position)

        // Update the tasks.txt file by writing the new task list
        openFileOutput(FILE_NAME, MODE_PRIVATE).use { fos ->
            taskList.forEach { task ->
                val taskString = "${task.title} | ${task.description} | ${task.date} | ${task.time}\n"
                fos.write(taskString.toByteArray())
            }
        }

        // Send a notification about the task deletion
        NotificationHelper.sendNotificationDelete(this, taskToDelete)

        // Notify the adapter about the item being removed
        taskAdapter.notifyItemRemoved(position)
    }

}