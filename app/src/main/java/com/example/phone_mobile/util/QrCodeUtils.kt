package com.example.phone_mobile.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

object QrCodeUtils {

    /**
     * Tạo chuỗi định dạng vCard 3.0 chuẩn quốc tế.
     * Khi iPhone (iOS Camera) hoặc Android quét mã này, máy sẽ tự động
     * hiển thị thông báo "Thêm vào Danh bạ" (Add to Contacts).
     */
    fun buildVCardString(
        name: String,
        title: String = "",
        phone: String = "",
        email: String = ""
    ): String {
        val cleanPhone = phone.replace(" ", "")
        return buildString {
            appendLine("BEGIN:VCARD")
            appendLine("VERSION:3.0")
            appendLine("FN:$name")
            appendLine("N:;$name;;;")
            if (title.isNotBlank()) {
                appendLine("TITLE:$title")
            }
            if (cleanPhone.isNotBlank()) {
                appendLine("TEL;TYPE=CELL:$cleanPhone")
            }
            if (email.isNotBlank()) {
                appendLine("EMAIL;TYPE=INTERNET:$email")
            }
            append("END:VCARD")
        }
    }

    /**
     * Sinh ảnh Bitmap mã QR thật từ dữ liệu text/vCard với mức sửa lỗi cao (ErrorCorrectionLevel.H).
     */
    fun generateQrBitmap(
        content: String,
        width: Int = 600,
        height: Int = 600
    ): ImageBitmap? {
        return try {
            val hints = mapOf(
                EncodeHintType.CHARACTER_SET to "UTF-8",
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H,
                EncodeHintType.MARGIN to 1
            )
            val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            val matrixWidth = bitMatrix.width
            val matrixHeight = bitMatrix.height
            val pixels = IntArray(matrixWidth * matrixHeight)

            for (y in 0 until matrixHeight) {
                val offset = y * matrixWidth
                for (x in 0 until matrixWidth) {
                    pixels[offset + x] = if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
                }
            }

            val bitmap = Bitmap.createBitmap(matrixWidth, matrixHeight, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, matrixWidth, 0, 0, matrixWidth, matrixHeight)
            bitmap.asImageBitmap()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
