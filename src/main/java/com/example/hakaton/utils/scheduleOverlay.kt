package com.example.hakaton.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import kotlin.jvm.java

fun scheduleOverlay(context: Context, folderIds: IntArray, delaySec: Long) {
    val am = context.getSystemService<AlarmManager>()!!
    val intent = Intent(context, OverlayReceiver::class.javaClass).apply {
        putExtra("folderIds", folderIds)
    }
    val pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    am.setExactAndAllowWhileIdle(
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        SystemClock.elapsedRealtime() + delaySec * 1000,
        pi
    )
}