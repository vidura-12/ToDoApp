package com.example.taskapp

import Task
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val FILE_NAME = "tasks.txt"
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val titleInput: EditText = findViewById(R.id.taskTitleInput)
        val descriptionInput: EditText = findViewById(R.id.taskDescriptionInput)
        val dateInput: EditText = findViewById(R.id.taskDateInput)
        val timeInput: EditText = findViewById(R.id.taskTimeInput)
        val saveButton: Button = findViewById(R.id.saveTaskButton)
        val viewTasksButton: Button = findViewById(R.id.viewTasksButton)

        // Create Notification Channel
        NotificationHelper.createNotificationChannel(this)

        // Check for notification permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST_CODE)
        }

        // Set up Date Picker
        dateInput.setOnClickListener {
            showDatePicker(dateInput)
        }

        // Set up Time Picker
        timeInput.setOnClickListener {
            showTimePicker(timeInput)
        }

        saveButton.setOnClickListener {
            val title = titleInput.text.toString()
            val description = descriptionInput.text.toString()
            val date = dateInput.text.toString()
            val time = timeInput.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty()) {
                val task = Task(title, description, date, time)
                saveTask(task)

                // Send notification
                NotificationHelper.sendNotification(this, task)

                // Clear the EditText fields after saving the task
                titleInput.text.clear()
                descriptionInput.text.clear()
                dateInput.text.clear()
                timeInput.text.clear()

                Toast.makeText(this, "Task Saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        viewTasksButton.setOnClickListener {
            val intent = Intent(this, TaskListActivity::class.java)
            startActivity(intent)
        }
        val imageView: ImageView = findViewById(R.id.imageView23)
        imageView.setOnClickListener {
            val intent = Intent(this, TaskListActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveTask(task: Task) {
        val taskString = "${task.title} | ${task.description} | ${task.date} | ${task.time}\n"
        openFileOutput(FILE_NAME, Context.MODE_APPEND).use { fos ->
            OutputStreamWriter(fos).use { writer ->
                writer.write(taskString)
            }
        }
    }

    private fun showDatePicker(dateInput: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            val date = dateFormat.format(selectedDate.time)
            dateInput.setText(date)
        }, year, month, day).show()
    }

    private fun showTimePicker(timeInput: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            val selectedTime = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, selectedHour)
                set(Calendar.MINUTE, selectedMinute)
            }
            val time = timeFormat.format(selectedTime.time)
            timeInput.setText(time)
        }, hour, minute, true).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}