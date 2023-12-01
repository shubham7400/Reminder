package com.example.reminder

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.reminder.databinding.ActivityMainBinding
import java.util.*

/*

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private lateinit var viewModel: ReminderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[ReminderViewModel::class.java]

        setupUI()
    }

    private fun setupUI() {
        // Add button click listener
        binding.addButton.setOnClickListener {
            val title = binding.titleEditText.text.toString().trim()
            if (title.isNotEmpty()) {
                val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(binding.dateTimeEditText.text.toString())
                val reminder = Reminder(title = title, dateTime = dateTime.time)
                viewModel.insert(reminder)
                clearInputFields()
            }
        }

        // Observe the LiveData and update the UI
        viewModel.allReminders.observe(this) { reminders ->
            // Update UI with the list of reminders
            // For simplicity, you can display them in a TextView or a RecyclerView
            binding.remindersTextView.text = reminders.joinToString("\n") { it.title }
        }
    }

    private fun clearInputFields() {
        binding.titleEditText.text.clear()
        binding.dateTimeEditText.text.clear()
    }
}
*/


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    var date: Long = 0
    var time: Long = 0

    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0

    private var mmYear = 0
    private var mmMonth = 0
    private var mmDay = 0
    private var mmHour = 0
    private var mmMinute = 0


    private lateinit var viewModel: ReminderViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setOnClickListeners()
        viewModel = ViewModelProvider(this)[ReminderViewModel::class.java]


        val reminderDao = ReminderDatabase.getDatabase(application).reminderDao()
        val repository = ReminderRepository(reminderDao)

        val adapter = ReminderAdapter(reminders = emptyList(), repository)
        binding.rvReminders.adapter = adapter

        viewModel.allReminders.observe(this){
            adapter.sumbitList(it)
            adapter.notifyDataSetChanged()
        }

        viewModel.getAllReminders()
    }

     private fun setOnClickListeners() {
        binding.btnDate.setOnClickListener {
            // Get Current Date
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(this,
                { view, year, monthOfYear, dayOfMonth -> binding.inDate.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    mmYear = year
                    mmMonth = monthOfYear
                    mmDay = dayOfMonth
                    val calendar = Calendar.getInstance()
                    calendar[Calendar.YEAR] = year
                    calendar[Calendar.MONTH] = monthOfYear
                    calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                    calendar[Calendar.HOUR_OF_DAY] = 0
                    calendar[Calendar.MINUTE] = 0
                    calendar[Calendar.SECOND] = 0
                    calendar[Calendar.MILLISECOND] = 0
                    println("ddfdfd  date ${calendar.timeInMillis}   ${System.currentTimeMillis()}")
                    date = calendar.timeInMillis
                },
                mYear,
                mMonth,
                mDay
            )
            datePickerDialog.show()
        }

        binding.btnReminder.setOnClickListener {
            val dateAndTimeMillisecond = date + time


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Code to be executed on Android 13 and above
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) -> {
                        // You can use the API that requires the permission.
                        setAlarm(dateAndTimeMillisecond)
                        refresFields()
                    }
                    else -> {
                        // You can directly ask for the permission.
                        // The registered ActivityResultCallback gets the result of this request.
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            } else {
                // Code for versions below Android 13
                setAlarm(dateAndTimeMillisecond)
                refresFields()
            }

        }
        binding.btnTime.setOnClickListener {
            // Get Current Time
            val c = Calendar.getInstance()
            mHour = c[Calendar.HOUR_OF_DAY]
            mMinute = c[Calendar.MINUTE]

            // Launch Time Picker Dialog
            val timePickerDialog = TimePickerDialog(this,
                {
                        view, hourOfDay, minute -> binding.inTime.setText("$hourOfDay:$minute")

                    mmHour = hourOfDay
                    mmMinute = minute
                    val calendar = Calendar.getInstance()
                    calendar[Calendar.HOUR_OF_DAY] = hourOfDay
                    calendar[Calendar.MINUTE] = minute
                    calendar[Calendar.SECOND] = 0
                    calendar[Calendar.MILLISECOND] = 0

                    // Get the time in milliseconds

                    // Get the time in milliseconds
                    time = calendar.timeInMillis
                    println("ddfdfd  time  ${c.timeInMillis}")
                },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.show()
        }
      }

    private fun refresFields() {
        binding.inDate.setText("")
        binding.inTime.setText("")
        Toast.makeText(this, "Alarm set successfully", Toast.LENGTH_SHORT).show()
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            binding.btnReminder.performClick()
        } else {
            Toast.makeText(this, "Please allow the alarm permission", Toast.LENGTH_SHORT).show()
            //toast(getString(R.string.please_allow_the_alarm_permission))
        }
    }


    private fun setAlarm(dateAndTimeMillisecond: Long) {

        // Generate a unique request code
        val requestCode = System.currentTimeMillis().toInt()

        val alarmManager: AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MONTH, mmMonth);
        calendar.set(Calendar.YEAR, mmYear);
        calendar.set(Calendar.DAY_OF_MONTH, mmDay);
        calendar.set(Calendar.HOUR_OF_DAY, mmHour);
        calendar.set(Calendar.MINUTE, mmMinute);
        calendar.set(Calendar.SECOND, 0);


        val intent = Intent(this, AlarmReceiver::class.java)




        val pandingIntent: PendingIntent = PendingIntent.getBroadcast(this, requestCode, intent, PendingIntent.FLAG_IMMUTABLE)
        val date = Date(dateAndTimeMillisecond)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis , pandingIntent)

        val reminder = Reminder(id = requestCode, title = "My Reminder", dateTime = calendar.timeInMillis)
        viewModel.insert(reminder)
        viewModel.getAllReminders()
    }
}
