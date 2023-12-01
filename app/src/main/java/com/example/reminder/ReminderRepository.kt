package com.example.reminder

import androidx.lifecycle.LiveData

class ReminderRepository(private val reminderDao: ReminderDao) {

    val allReminders: LiveData<List<Reminder>> = reminderDao.getAllReminders()

    suspend fun insert(reminder: Reminder) {
        reminderDao.insert(reminder)
    }

    suspend fun deleteReminder(id: Int){
        reminderDao.deleteById(id = id)
    }
}
