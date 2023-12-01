package com.example.reminder

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ReminderDao {
    @Insert
    suspend fun insert(reminder: Reminder)

    @Query("SELECT * FROM reminders ORDER BY dateTime ASC")
    fun getAllReminders(): LiveData<List<Reminder>>

    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Int)
}
