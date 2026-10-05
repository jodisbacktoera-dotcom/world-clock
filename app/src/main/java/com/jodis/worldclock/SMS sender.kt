package com.jodis.worldclock

import android.telephony.SmsManager
import android.util.Log

object SmsSender {

    private const val PREFIX = "#WC#"

    fun sendMessage(phoneNumber: String, message: String) {
        try {
            val smsManager = SmsManager.getDefault()
            val fullMessage = PREFIX + message
            smsManager.sendTextMessage(phoneNumber, null, fullMessage, null, null)
            Log.d("SmsSender", "Sent to $phoneNumber: $message")
        } catch (e: Exception) {
            Log.e("SmsSender", "Error: ${e.message}")
        }
    }

    fun parseIncoming(rawMessage: String): String? {
        return if (rawMessage.startsWith(PREFIX)) {
            rawMessage.removePrefix(PREFIX)
        } else {
            null
        }
    }
}
