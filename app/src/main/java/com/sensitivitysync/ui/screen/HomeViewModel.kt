package com.sensitivitysync.ui.screen

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.hardware.display.DisplayManager
import android.media.projection.MediaProjectionManager
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sensitivitysync.calibration.AccelerationMatcher
import com.sensitivitysync.calibration.CalibrationEngine
import com.sensitivitysync.calibration.RotationDetector
import com.sensitivitysync.calibration.SensitivityConverter
import com.sensitivitysync.calibration.SwipeController
import com.sensitivitysync.data.CalibrationStep
import com.sensitivitysync.data.PermissionState
import com.sensitivitysync.data.Permissions
import com.sensitivitysync.service.CaptureService
import com.sensitivitysync.shizuku.ShizukuManager
import dev.rikka.shizuku.Shizuku
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val shizukuManager = ShizukuManager()
    private val swipeController = SwipeController(shizukuManager)
    private val rotationDetector = RotationDetector()
    private val accelMatcher = AccelerationMatcher()
    private val converter = SensitivityConverter()
    val engine = CalibrationEngine(
        swipeController, rotationDetector, accelMatcher, converter
    )

    private val _permissions = MutableStateFlow(Permissions())
    val permissions: StateFlow<Permissions> = _permissions.asStateFlow()

    private val _displayMetrics = MutableStateFlow(DisplayMetrics())
    val displayMetrics: StateFlow<DisplayMetrics> = _displayMetrics.asStateFlow()

    val session = engine.session
    val status = engine.status

    private var mediaProjectionIntent: Intent? = null
    private var mediaProjectionResultCode: Int = Activity.RESULT_CANCELED

    init {
        checkShizuku()
        loadDisplayMetrics()
    }

    private fun checkShizuku() {
        if (shizukuManager.isAvailable && shizukuManager.isRunning) {
            _permissions.value = _permissions.value.copy(shizuku = PermissionState.GRANTED)
        }
    }

    private fun loadDisplayMetrics() {
        val ctx = getApplication<Application>()
        val wm = ctx.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        wm.defaultDisplay.getRealMetrics(metrics)
        _displayMetrics.value = metrics
        swipeController.setScreenSize(metrics.widthPixels, metrics.heightPixels)
    }

    fun requestShizuku() {
        try {
            Shizuku.requestPermission(REQUEST_SHIZUKU)
        } catch (_: Exception) {
            _permissions.value = _permissions.value.copy(shizuku = PermissionState.DENIED)
        }
    }

    fun onShizukuGranted() {
        _permissions.value = _permissions.value.copy(shizuku = PermissionState.GRANTED)
        shizukuManager.init()
    }

    fun requestMediaProjection(activity: Activity) {
        val mpManager = activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE)
                as MediaProjectionManager
        val intent = mpManager.createScreenCaptureIntent()
        activity.startActivityForResult(intent, REQUEST_MEDIA_PROJECTION)
    }

    fun onMediaProjectionResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            mediaProjectionResultCode = resultCode
            mediaProjectionIntent = data
            _permissions.value = _permissions.value.copy(mediaProjection = PermissionState.GRANTED)
            startCaptureService()
        } else {
            _permissions.value = _permissions.value.copy(mediaProjection = PermissionState.DENIED)
        }
    }

    fun requestOverlayPermission(activity: Activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            val intent = Intent(
                android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                android.net.Uri.parse("package:${activity.packageName}")
            )
            activity.startActivity(intent)
        }
    }

    fun onOverlayPermissionResult(granted: Boolean) {
        _permissions.value = _permissions.value.copy(
            overlay = if (granted) PermissionState.GRANTED else PermissionState.DENIED
        )
    }

    private fun startCaptureService() {
        val ctx = getApplication<Application>()
        val intent = Intent(ctx, CaptureService::class.java).apply {
            putExtra(CaptureService.EXTRA_RESULT_CODE, mediaProjectionResultCode)
            putExtra(CaptureService.EXTRA_DATA, mediaProjectionIntent)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ctx.startForegroundService(intent)
        } else {
            ctx.startService(intent)
        }
    }

    fun startCalibration(baseSensA: Float, accelA: Int) {
        engine.startNewSession()
        engine.setGameAInput(baseSensA, accelA)
        viewModelScope.launch {
            // Calibration flow managed by engine + UI interaction
        }
    }

    fun requestNotificationPermission(activity: Activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            activity.requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_NOTIFICATION
            )
        } else {
            _permissions.value = _permissions.value.copy(notification = PermissionState.GRANTED)
        }
    }

    companion object {
        private const val REQUEST_SHIZUKU = 1000
        private const val REQUEST_MEDIA_PROJECTION = 1001
        private const val REQUEST_NOTIFICATION = 1002
    }
}
