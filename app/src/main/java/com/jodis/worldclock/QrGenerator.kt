package com.jodis.worldclock

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

object QrGenerator {

    fun generateQr(text: String, size: Int = 600): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
            for (x in 0 until size) {
                for (y in 0 until size) {
                    bitmap.setPixel(
                        x, y,
                        if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                    )
                }
            }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    // QR data format: WC|username|displayName|encryptedPhone
    fun buildQrData(username: String, displayName: String, phone: String): String {
        val encryptedPhone = CryptoHelper.encrypt(phone)
        return "WC|$username|$displayName|$encryptedPhone"
    }

    // QR data parse karo
    fun parseQrData(data: String): Triple<String, String, String>? {
        return try {
            val parts = data.split("|")
            if (parts.size == 4 && parts[0] == "WC") {
                val username = parts[1]
                val displayName = parts[2]
                val phone = CryptoHelper.decrypt(parts[3])
                if (phone.isNotEmpty()) {
                    Triple(username, displayName, phone)
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
