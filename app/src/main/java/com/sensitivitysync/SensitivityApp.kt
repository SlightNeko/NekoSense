package com.sensitivitysync

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import java.io.FileWriter

class SensitivityApp : Application() {
    override fun onCreate() {
        super.onCreate()
        installCrashHandler()
        createNotificationChannel()
    }

    private fun installCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            try {
                val crashFile = filesDir.resolve("crash.log")
                FileWriter(crashFile).use { w ->
                    w.write("=== CRASH ===\n")
                    w.write("Time: ${System.currentTimeMillis()}\n")
                    w.write("Thread: ${thread.name}\n")
                    w.write("${throwable.javaClass.name}: ${throwable.message}\n")
                    for (ste in throwable.stackTrace) {
                        w.write("\tat $ste\n")
                    }
                    throwable.cause?.let { cause ->
                        w.write("Caused by: ${cause.javaClass.name}: ${cause.message}\n")
                        for (ste in cause.stackTrace) {
                            w.write("\tat $ste\n")
                        }
                    }
                }
            } catch (_: Exception) {
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_CAPTURE,
                "Screen Capture",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Screen capture service notification"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_CAPTURE = "capture_service"
    }
}
