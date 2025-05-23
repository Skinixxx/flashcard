package com.example.hakaton.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // создаём канал (однократно безопасно)
        NotificationHelper.createChannel(context)

        // случайный ID
        val id = (0..10000).random()
        NotificationHelper.sendReminder(context, id)
    }
}
