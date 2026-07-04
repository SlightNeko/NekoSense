package com.sensitivitysync.shizuku

import android.os.IBinder
import android.os.RemoteException
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

class ShizukuManager {

    val isAvailable: Boolean get() = Shizuku.pingBinder()

    val isRunning: Boolean get() = Shizuku.getVersion() > 0

    fun init() {
        // initialized via Shizuku API
    }

    suspend fun executeCommand(command: String): ShellResult = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(
                arrayOf("sh", "-c", "shizuku sh -c '$command'")
            )
            val stdout = BufferedReader(InputStreamReader(process.inputStream)).readText()
            val stderr = BufferedReader(InputStreamReader(process.errorStream)).readText()
            val exitCode = process.waitFor()
            ShellResult(exitCode, stdout.trim(), stderr.trim())
        } catch (e: Exception) {
            ShellResult(-1, "", e.message ?: "Unknown error")
        }
    }

    suspend fun inputSwipe(
        x1: Int, y1: Int, x2: Int, y2: Int, durationMs: Int
    ): ShellResult {
        return executeCommand("input swipe $x1 $y1 $x2 $y2 $durationMs")
    }

    suspend fun inputTap(x: Int, y: Int): ShellResult {
        return executeCommand("input tap $x $y")
    }
}

data class ShellResult(
    val exitCode: Int,
    val stdout: String,
    val stderr: String
) {
    val isSuccess: Boolean get() = exitCode == 0
}
