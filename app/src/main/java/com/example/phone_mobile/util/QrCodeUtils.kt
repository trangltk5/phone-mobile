package com.example.phone_mobile.util

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.phone_mobile.data.model.Contact
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.UUID

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
     * Phân tích chuỗi QR (vCard 2.1/3.0/4.0, MECARD hoặc text) thành đối tượng Contact.
     */
    fun parseContactFromRawText(raw: String): Contact {
        val clean = raw.trim()
        var name = ""
        var phone = ""
        var email = ""
        var title = ""
        var address = ""
        var note = ""

        if (clean.contains("BEGIN:VCARD", ignoreCase = true)) {
            val lines = clean.lines()
            for (line in lines) {
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("FN:", ignoreCase = true) -> {
                        name = trimmed.substring(3).trim()
                    }
                    trimmed.startsWith("N:", ignoreCase = true) && name.isBlank() -> {
                        val rawN = trimmed.substring(2).trim()
                        val parts = rawN.split(";").filter { it.isNotBlank() }
                        name = parts.joinToString(" ")
                    }
                    trimmed.startsWith("TEL", ignoreCase = true) -> {
                        val colonIdx = trimmed.indexOf(":")
                        if (colonIdx != -1) {
                            val num = trimmed.substring(colonIdx + 1).trim()
                            if (phone.isBlank()) phone = num
                        }
                    }
                    trimmed.startsWith("EMAIL", ignoreCase = true) -> {
                        val colonIdx = trimmed.indexOf(":")
                        if (colonIdx != -1) {
                            val mail = trimmed.substring(colonIdx + 1).trim()
                            if (email.isBlank()) email = mail
                        }
                    }
                    trimmed.startsWith("TITLE:", ignoreCase = true) -> {
                        title = trimmed.substring(6).trim()
                    }
                    trimmed.startsWith("NOTE:", ignoreCase = true) -> {
                        note = trimmed.substring(5).trim()
                    }
                    trimmed.startsWith("ADR", ignoreCase = true) -> {
                        val colonIdx = trimmed.indexOf(":")
                        if (colonIdx != -1) {
                            address = trimmed.substring(colonIdx + 1).replace(";", " ").trim()
                        }
                    }
                }
            }
        } else if (clean.startsWith("MECARD:", ignoreCase = true)) {
            val parts = clean.removePrefix("MECARD:").removePrefix("mecard:").split(";")
            for (part in parts) {
                val p = part.trim()
                when {
                    p.startsWith("N:") -> name = p.substring(2).trim()
                    p.startsWith("TEL:") -> phone = p.substring(4).trim()
                    p.startsWith("EMAIL:") -> email = p.substring(6).trim()
                    p.startsWith("NOTE:") -> note = p.substring(5).trim()
                    p.startsWith("ADR:") -> address = p.substring(4).trim()
                }
            }
        } else if (clean.matches(Regex("^[+0-9\\s()-]{7,}$"))) {
            phone = clean
            name = "Liên hệ $clean"
        } else {
            val lines = clean.lines().filter { it.isNotBlank() }
            if (lines.isNotEmpty()) {
                name = lines.first()
                if (lines.size > 1) {
                    val candidatePhone = lines.find { it.any { c -> c.isDigit() } }
                    if (candidatePhone != null) phone = candidatePhone
                }
            }
        }

        if (name.isBlank()) name = "Liên hệ mới từ QR"
        if (phone.isBlank()) phone = "0987 654 321"

        val finalNote = if (title.isNotBlank()) {
            if (note.isNotBlank()) "$title • $note" else title
        } else note

        return Contact(
            id = UUID.randomUUID().toString(),
            name = name,
            phoneNumber = phone,
            email = email,
            address = address,
            note = finalNote
        )
    }

    /**
     * Giải mã QR code từ một Bitmap (ví dụ ảnh chọn từ thư viện ảnh / Gallery).
     */
    fun decodeQrFromBitmap(bitmap: Bitmap): String? {
        return try {
            val width = bitmap.width
            val height = bitmap.height
            val pixels = IntArray(width * height)
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
            val source = RGBLuminanceSource(width, height, pixels)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val result = MultiFormatReader().decode(binaryBitmap)
            result.text
        } catch (e: Exception) {
            null
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
