package com.example.reminder

// ReminderAdapter.kt
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReminderAdapter(private var reminders: List<Reminder>, private var repository: ReminderRepository) :
    RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {



    inner class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val dateTimeTextView: TextView = itemView.findViewById(R.id.dateTimeTextView)
        val cancelReminderButton: Button = itemView.findViewById(R.id.btn_cancel_reminder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.reminder_item, parent, false)
        return ReminderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.titleTextView.text = reminder.title
        holder.dateTimeTextView.text =
            "Date and Time: ${formatDateTime(reminder.dateTime)}"
        holder.cancelReminderButton.setOnClickListener {
            cancelAlarm(it.context, reminder.id)
        }
    }

    private  fun cancelAlarm(context: Context, id: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel the alarm using the same PendingIntent
        alarmManager.cancel(pendingIntent)
        Toast.makeText(context, "Reminder Canceled.", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.Default).launch {
            repository.deleteReminder(id = id)
            repository.allReminders
        }

    }

    override fun getItemCount(): Int {
        return reminders.size
    }

    private fun formatDateTime(dateTimeMillis: Long): String {
        // Implement your date and time formatting logic here
        // For simplicity, using a basic formatting example
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateTimeMillis

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return "${dateFormat.format(calendar.time)} ${timeFormat.format(calendar.time)}"
    }

    fun sumbitList(it: List<Reminder>) {
        reminders = it
    }
}
