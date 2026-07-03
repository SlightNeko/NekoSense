package com.sensitivitysync.service

import android.content.Context
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.sensitivitysync.ui.component.CalibrationOverlay

class FloatManager(private val context: Context) {

    private val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null

    private var params: WindowManager.LayoutParams = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT
    ).apply {
        gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        y = (100 * context.resources.displayMetrics.density).toInt()
    }

    fun showOverlay(content: @Composable () -> Unit) {
        hideOverlay()
        val composeView = ComposeView(context).apply {
            setContent(content)
        }
        overlayView = composeView
        wm.addView(composeView, params)
    }

    fun hideOverlay() {
        overlayView?.let {
            wm.removeView(it)
            overlayView = null
        }
    }

    fun updateVisibility(visible: Boolean) {
        overlayView?.visibility = if (visible) View.VISIBLE else View.GONE
    }
}
