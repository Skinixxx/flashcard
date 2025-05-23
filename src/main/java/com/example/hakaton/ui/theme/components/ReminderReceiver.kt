package com.example.hakaton.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.hakaton.ui.screens.OverlayActivity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Убедимся, что канал создан
        NotificationHelper.createChannel(context)

        // Генерируем случайный ID (чтобы не перезаписывать уведомления)
        val id = (0..Int.MAX_VALUE).random()
        NotificationHelper.sendReminder(context, id)
    }
}


class OverlayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val folderIds = intent?.getIntArrayExtra("folderIds") ?: return
        val overlayIntent = Intent(context, OverlayActivity::class.java).apply {
            putExtra("folderIds", folderIds)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(overlayIntent)
    }
}
