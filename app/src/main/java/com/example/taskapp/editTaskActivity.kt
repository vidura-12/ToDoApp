package com.example.taskapp

import Task
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class editTaskActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_task)
        val imageView: ImageView = findViewById(R.id.imageView2)
        imageView.setOnClickListener {
            val intent = Intent(this, TaskListActivity::class.java)
            startActivity(intent)
        }
        val editTitle: EditText = findViewById(R.id.editTaskTitle)
        val editDescription: EditText = findViewById(R.id.editTaskDescription)
        val editDate: EditText = findViewById(R.id.editTaskDate)
        val editTime: EditText = findViewById(R.id.editTaskTime)
        val saveButton: Button = findViewById(R.id.saveTaskButton)

        // Retrieve the task details from the intent
        val task = intent.getParcelableExtra<Task>("task") ?: return

        // Populate fields with task details
        editTitle.setText(task.title)
        editDescription.setText(task.description)
        editDate.setText(task.date)
        editTime.setText(task.time)

        // Save the edited task
        saveButton.setOnClickListener {
            val updatedTask = Task(
                id = task.id,
                title = editTitle.text.toString(),
                description = editDescription.text.toString(),
                date = editDate.text.toString(),
                time = editTime.text.toString()
            )

            // Pass the updated task back to the TaskListActivity
            val resultIntent = Intent().apply {
                putExtra("updatedTask", updatedTask)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}