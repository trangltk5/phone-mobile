package com.example.phone_mobile.util

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Quản lý thông báo nổi ở Top Center của ứng dụng
 */
object NotificationManager {
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 5)
    val messages = _messages.asSharedFlow()

    fun show(context: Context, message: String) {
        // 1. Phát sự kiện để hiển thị banner Compose ở Top Center
        _messages.tryEmit(message)

        // 2. Đồng thời gọi Toast hệ thống với vị trí TOP | CENTER_HORIZONTAL
        try {
            val toast = Toast.makeText(context.applicationContext, message, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 160)
            toast.show()
        } catch (_: Exception) {}
    }
}
