package com.example.taskapp


import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.io.BufferedReader
import java.io.InputStreamReader

class TaskAppWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Loop through all widgets
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val widgetText = loadTaskListFromFile(context) // Load tasks from file

            // Create an intent to launch the app when the widget is clicked
            val intent = Intent(context, TaskListActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_task_list)
            views.setTextViewText(R.id.widgetTaskList, widgetText)

            // Set onClickListener for the widget to launch the main activity
            views.setOnClickPendingIntent(R.id.widgetTaskList, pendingIntent)

            // Update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        // Function to load tasks from internal storage and return as a string
        private fun loadTaskListFromFile(context: Context): String {
            val fileName = "tasks.txt"
            val file = context.getFileStreamPath(fileName)
            if (!file.exists()) {
                return "No tasks"
            }

            val taskList = mutableListOf<String>()
            context.openFileInput(fileName).use { fis ->
                BufferedReader(InputStreamReader(fis)).use { reader ->
                    reader.forEachLine { line ->
                        val taskDetails = line.split(" | ")
                        if (taskDetails.size == 4) {
                            taskList.add(taskDetails[0]) // Add task title to the list
                        }
                    }
                }
            }

            return if (taskList.isNotEmpty()) {
                taskList.joinToString(separator = "\n") // Display each task on a new line
            } else {
                "No tasks"
            }
        }
    }
}
