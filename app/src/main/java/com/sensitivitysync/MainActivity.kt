package com.sensitivitysync

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("NekoSense", "MainActivity.onCreate started")
        try {
            setContent {
                Text("Hello NekoSense")
            }
            Log.e("NekoSense", "MainActivity.onCreate completed")
        } catch (e: Exception) {
            Log.e("NekoSense", "CRASH in setContent", e)
            throw e
        }
    }
}
