package com.jodis.worldclock

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Telephony
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class VerifyPhoneActivity : AppCompatActivity() {

    private var generatedOtp: String = ""
    private lateinit var phone: String
    private lateinit var username: String
    private lateinit var displayName: String
    private lateinit var pin: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)

        phone = intent.getStringExtra("phone") ?: ""
        username = intent.getStringExtra("username") ?: ""
        displayName = intent.getStringExtra("displayName") ?: ""
        pin = intent.getStringExtra("pin") ?: ""

        if (phone.isEmpty()) {
            Toast.makeText(this, "Phone number missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        generatedOtp = (100000 + Random.nextInt(900000)).toString()
        sendOtpSms()

        val etOtp = findViewById<EditText>(R.id.etOtp)
        val btnVerify = findViewById<Button>(R.id.btnVerify)
        val btnResend = findViewById<TextView>(R.id.btnResend)

        btnVerify.setOnClickListener {
            val enteredOtp = etOtp.text.toString().trim()
            if (enteredOtp == generatedOtp) {
                UserData.saveUser(this, username, displayName, pin, phone)
                Toast.makeText(this, "Number verify ho gaya! ✅", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Galat code", Toast.LENGTH_SHORT).show()
                etOtp.text.clear()
            }
        }

        btnResend.setOnClickListener {
            generatedOtp = (100000 + Random.nextInt(900000)).toString()
            sendOtpSms()
            Toast.makeText(this, "Naya code bheja gaya", Toast.LENGTH_SHORT).show()
            etOtp.text.clear()
            startOtpAutoReader()
        }

        // Auto-read OTP from inbox
        startOtpAutoReader()
    }

    private fun sendOtpSms() {
        val message = "VERIFY:$generatedOtp"
        SmsSender.sendMessage(phone, message)
    }

    private var readerHandler: Handler? = null
    private var readerRunnable: Runnable? = null

    private fun startOtpAutoReader() {
        readerHandler = Handler(Looper.getMainLooper())
        var attempts = 0
        readerRunnable = object : Runnable {
            override fun run() {
                attempts++
                val otpFromInbox = readOtpFromInbox()
                if (otpFromInbox != null) {
                    val etOtp = findViewById<EditText>(R.id.etOtp)
                    etOtp.setText(otpFromInbox)
                    Toast.makeText(
                        this@VerifyPhoneActivity,
                        "Code auto-fill ho gaya ✅",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (attempts < 30) {
                    readerHandler?.postDelayed(this, 1000)
                }
            }
        }
        readerHandler?.post(readerRunnable!!)
    }

    // Inbox se last SMS padho aur OTP nikalo
    private fun readOtpFromInbox(): String? {
        return try {
            val cursor = contentResolver.query(
                Telephony.Sms.Inbox.CONTENT_URI,
                null, null, null,
                Telephony.Sms.DEFAULT_SORT_ORDER
            )
            cursor?.use {
                if (it.moveToFirst()) {
                    val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    // Dhundho: "#WC#VERIFY:123456" ya "VERIFY:123456"
                    if (body.contains("VERIFY:")) {
                        val code = body.substringAfter("VERIFY:").trim().take(6)
                        if (code.length == 6 && code.all { c -> c.isDigit() }) {
                            return code
                        }
                    }
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        readerRunnable?.let { readerHandler?.removeCallbacks(it) }
    }
}
