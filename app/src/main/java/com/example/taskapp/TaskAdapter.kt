import Task
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskapp.R

class TaskAdapter(
    private val tasks: MutableList<Task>,  // MutableList to allow task removal
    private val onLongClick: (Int) -> Unit,  // Lambda to handle long-clicks
    private val onClick: (Int) -> Unit  // Lambda to handle single taps
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitle)
        val taskDescription: TextView = itemView.findViewById(R.id.taskDescription)
        val taskDateTime: TextView = itemView.findViewById(R.id.taskDateTime)

        init {
            // Set long-click listener to trigger the callback function
            itemView.setOnLongClickListener {
                onLongClick(bindingAdapterPosition)
                true
            }

            // Set click listener to trigger the callback function
            itemView.setOnClickListener {
                onClick(bindingAdapterPosition)
            }
        }
    }

    // Inflate the layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    // Bind the data to the view holder
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.taskTitle.text = task.title
        holder.taskDescription.text = task.description
        holder.taskDateTime.text = "${task.date} ${task.time}"
    }

    // Return the total number of items
    override fun getItemCount(): Int {
        return tasks.size
    }
}
