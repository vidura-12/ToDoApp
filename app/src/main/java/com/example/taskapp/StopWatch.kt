package com.example.taskapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.taskapp.databinding.ActivityStopWatchBinding

class StopWatch : AppCompatActivity() {

    private lateinit var binding: ActivityStopWatchBinding
    private var startTime: Long = 0
    private var timeBuffer: Long = 0
    private var updatedTime: Long = 0
    private var handler: Handler = Handler()
    private var running: Boolean = false
    private var pauseTime: Long = 0

    private val updateTimer: Runnable = object : Runnable {
        override fun run() {
            updatedTime = SystemClock.uptimeMillis() - startTime + timeBuffer

            val seconds = (updatedTime / 1000).toInt() % 60
            val minutes = (updatedTime / 60000).toInt() % 60
            val hours = (updatedTime / 3600000).toInt()
            val milliseconds = (updatedTime % 1000).toInt() / 10  // Show milliseconds in 2 digits

            binding.stopwatchTime.text = String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, milliseconds)
            handler.postDelayed(this, 10) // Update every 10ms to display milliseconds
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStopWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val imageView: ImageView = findViewById(R.id.imageView2)
        imageView.setOnClickListener {
            val intent = Intent(this, TaskListActivity::class.java)
            startActivity(intent)
        }
        binding.startButton.setOnClickListener {
            if (!running) {
                startTime = SystemClock.uptimeMillis()
                handler.post(updateTimer)
                running = true
            }
        }

        binding.pauseButton.setOnClickListener {
            if (running) {
                timeBuffer += updatedTime
                handler.removeCallbacks(updateTimer)
                running = false
            }
        }

        binding.resetButton.setOnClickListener {
            handler.removeCallbacks(updateTimer)
            binding.stopwatchTime.text = "00:00:00:00"
            startTime = 0
            timeBuffer = 0
            updatedTime = 0
            running = false
        }
    }
}
