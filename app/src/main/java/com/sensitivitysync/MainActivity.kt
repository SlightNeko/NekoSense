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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sensitivitysync.ui.screen.HomeScreen
import com.sensitivitysync.ui.screen.HomeViewModel
import com.sensitivitysync.ui.theme.NekoSenseTheme
import dev.rikka.shizuku.Shizuku

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: HomeViewModel

    private val mediaProjectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onMediaProjectionResult(result.resultCode, result.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModel()

        Shizuku.addRequestPermissionListener(requestPermissionListener)

        setContent {
            NekoSenseTheme {
                HomeScreen(
                    viewModel = viewModel,
                    onRequestShizuku = { requestShizuku() },
                    onRequestMediaProjection = { requestMediaProjection() },
                    onOpenOverlaySettings = { openOverlaySettings() }
                )
            }
        }
    }

    private fun requestShizuku() {
        if (Shizuku.isPreV11() || !Shizuku.pingBinder()) {
            Shizuku.shouldShowRequestPermissionRationale()
        }
        Shizuku.requestPermission(HomeViewModel.REQUEST_SHIZUKU)
    }

    private fun requestMediaProjection() {
        val intent = Intent(this, mediaProjectionLauncher.javaClass).apply {
            val mpManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            putExtra("intent", mpManager.createScreenCaptureIntent())
        }
        mediaProjectionLauncher.launch(
            (getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager)
                .createScreenCaptureIntent()
        )
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

    private val requestPermissionListener = object : Shizuku.OnRequestPermissionResultListener {
        override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
            if (requestCode == HomeViewModel.REQUEST_SHIZUKU) {
                if (grantResult == Shizuku.PERMISSION_GRANTED) {
                    viewModel.onShizukuGranted()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Shizuku.removeRequestPermissionListener(requestPermissionListener)
    }
}
