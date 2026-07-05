package com.sensitivitysync

import android.content.Intent
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import com.sensitivitysync.ui.screen.HomeScreen
import com.sensitivitysync.ui.screen.HomeViewModel
import com.sensitivitysync.ui.theme.NekoSenseTheme
import rikka.shizuku.Shizuku
import rikka.shizuku.Shizuku.OnRequestPermissionResultListener

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: HomeViewModel

    private val mediaProjectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onMediaProjectionResult(result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

            if (Shizuku.pingBinder()) {
                Shizuku.addRequestPermissionResultListener(requestPermissionListener)
            }
        } catch (e: Exception) {
            Log.e("NekoSense", "Init failed", e)
            throw e
        }

        setContent {
            NekoSenseTheme {
                MainScreen(viewModel)
            }
        }
    }

    private fun requestShizuku() {
        try {
            if (Shizuku.pingBinder()) {
                Shizuku.requestPermission(SHIZUKU_REQUEST_CODE)
            }
        } catch (e: Exception) {
            Log.e("NekoSense", "requestShizuku failed", e)
        }
    }

    private fun requestMediaProjection() {
        val mpManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjectionLauncher.launch(mpManager.createScreenCaptureIntent())
    }

    private fun openOverlaySettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivity(intent)
                return
            }
        }
        viewModel.onOverlayPermissionResult(true)
    }

    private val requestPermissionListener = OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == SHIZUKU_REQUEST_CODE && grantResult == PackageManager.PERMISSION_GRANTED) {
            viewModel.onShizukuGranted()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::viewModel.isInitialized && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(this)) {
                viewModel.onOverlayPermissionResult(true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (Shizuku.pingBinder()) {
                Shizuku.removeRequestPermissionResultListener(requestPermissionListener)
            }
        } catch (_: Exception) {
        }
    }

    @Composable
    private fun MainScreen(vm: HomeViewModel) {
        HomeScreen(
            viewModel = vm,
            onRequestShizuku = { requestShizuku() },
            onRequestMediaProjection = { requestMediaProjection() },
            onOpenOverlaySettings = { openOverlaySettings() }
        )
    }

    companion object {
        const val SHIZUKU_REQUEST_CODE = 1000
    }
}
