package com.sensitivitysync

import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import com.sensitivitysync.ui.screen.HomeScreen
import com.sensitivitysync.ui.screen.HomeViewModel
import com.sensitivitysync.ui.theme.NekoSenseTheme
import dev.rikka.shizuku.Shizuku
import dev.rikka.shizuku.Shizuku.OnRequestPermissionResultListener

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: HomeViewModel

    private val mediaProjectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onMediaProjectionResult(result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        if (Shizuku.pingBinder()) {
            Shizuku.addRequestPermissionListener(requestPermissionListener)
        }

        setContent {
            NekoSenseTheme {
                MainScreen(viewModel)
            }
        }
    }

    private fun requestShizuku() {
        Shizuku.requestPermission(SHIZUKU_REQUEST_CODE)
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
        if (requestCode == SHIZUKU_REQUEST_CODE && grantResult == Shizuku.PERMISSION_GRANTED) {
            viewModel.onShizukuGranted()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Shizuku.pingBinder()) {
            Shizuku.removeRequestPermissionListener(requestPermissionListener)
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
