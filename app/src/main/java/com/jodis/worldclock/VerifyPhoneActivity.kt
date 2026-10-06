package com.jodis.worldclock

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

        // OTP generate karo
        generatedOtp = (100000 + Random.nextInt(900000)).toString()

        // SMS bhejo
        sendOtpSms()

        val etOtp = findViewById<EditText>(R.id.etOtp)
        val btnVerify = findViewById<Button>(R.id.btnVerify)
        val btnResend = findViewById<TextView>(R.id.btnResend)

        btnVerify.setOnClickListener {
            val enteredOtp = etOtp.text.toString().trim()
            if (enteredOtp == generatedOtp) {
                // Verified! Ab user save karo
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
        }

        // 60 second baad auto-fill check (agar SMS aa gaya ho)
        startOtpListener()
    }

    private fun sendOtpSms() {
        // SMS body format: #WC#VERIFY:123456
        val message = "VERIFY:$generatedOtp"
        SmsSender.sendMessage(phone, message)
    }

    private var listenerHandler: Handler? = null
    private var listenerRunnable: Runnable? = null

    private fun startOtpListener() {
        // 30 second tak har 1 second check karo ki OTP aa gaya kya
        listenerHandler = Handler(Looper.getMainLooper())
        var attempts = 0
        listenerRunnable = object : Runnable {
            override fun run() {
                attempts++
                val receivedOtp = SmsReceiver.lastVerificationCode
                if (receivedOtp != null) {
                    SmsReceiver.lastVerificationCode = null
                    val etOtp = findViewById<EditText>(R.id.etOtp)
                    etOtp.setText(receivedOtp)
                    Toast.makeText(
                        this@VerifyPhoneActivity,
                        "Code auto-fill ho gaya",
                        Toast.LENGTH_SHORT
                    ).show()
                    return
                }
                if (attempts < 30) {
                    listenerHandler?.postDelayed(this, 1000)
                }
            }
        }
        listenerHandler?.post(listenerRunnable!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRunnable?.let { listenerHandler?.removeCallbacks(it) }
    }
}
