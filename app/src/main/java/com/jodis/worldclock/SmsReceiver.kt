package com.jodis.worldclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

class SmsReceiver : BroadcastReceiver() {

    companion object {
        var lastVerificationCode: String? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle: Bundle? = intent.extras
            if (bundle != null) {
                try {
                    val pdus = bundle.get("pdus") as Array<*>?
                    val format = bundle.getString("format")
                    pdus?.forEach { pdu ->
                        val smsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            SmsMessage.createFromPdu(pdu as ByteArray, format)
                        } else {
                            SmsMessage.createFromPdu(pdu as ByteArray)
                        }
                        val sender = smsMessage.originatingAddress ?: return@forEach
                        val rawMessage = smsMessage.messageBody ?: return@forEach

                        val cleanMessage = SmsSender.parseIncoming(rawMessage)
                        if (cleanMessage != null) {
                            // Verification code hai kya?
                            if (cleanMessage.startsWith("VERIFY:")) {
                                val code = cleanMessage.removePrefix("VERIFY:").trim()
                                lastVerificationCode = code
                                Log.d("SmsReceiver", "Verification code: $code")
                            } else {
                                // Normal message
                                handleMessage(sender, cleanMessage)
                            }
                            abortBroadcast()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SmsReceiver", "Error: ${e.message}")
                }
            }
        }
    }

    private fun handleMessage(sender: String, message: String) {
        val chat = MessageStore.chats.find {
            it.chatId == sender || it.chatId.endsWith(sender.takeLast(10))
        }
        val chatId = chat?.chatId ?: sender
        val newMessage = Message(
            senderId = sender,
            text = message,
            timestamp = System.currentTimeMillis(),
            isMine = false
        )
        MessageStore.addMessage(chatId, newMessage)
        Log.d("SmsReceiver", "Received from $sender: $message")
    }
}
