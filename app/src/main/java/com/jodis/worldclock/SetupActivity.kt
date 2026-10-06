package com.jodis.worldclock

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etDisplayName = findViewById<EditText>(R.id.etDisplayName)
        val etPin = findViewById<EditText>(R.id.etPin)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // 🎯 SIM se number auto-detect karo
        val detectedNumber = detectSimNumber()
        if (detectedNumber.isNotEmpty()) {
            etPhone.setText(detectedNumber)
            Toast.makeText(this, "SIM se number mila: $detectedNumber", Toast.LENGTH_LONG).show()
        } else {
            // Permission maango, phir dubara try
            askPhonePermission()
        }

        btnSave.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val displayName = etDisplayName.text.toString().trim()
            val pin = etPin.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            if (username.length < 3) {
                Toast.makeText(this, "Username 3+ characters ka hona chahiye", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (username.contains(" ")) {
                Toast.makeText(this, "Username me space nahi ho sakta", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (displayName.isEmpty()) {
                Toast.makeText(this, "Display name daalo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pin.length != 4) {
                Toast.makeText(this, "PIN 4 digit ka hona chahiye", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phone.length < 10) {
                Toast.makeText(this, "Sahi phone number daalo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 🎯 Verification skip — SIM se hi verify hua
            UserData.saveUser(this, username, displayName, pin, phone)
            Toast.makeText(this, "Setup complete! ✅", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun detectSimNumber(): String {
        return try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return ""
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val subscriptionManager =
                    getSystemService(SubscriptionManager::class.java)
                if (subscriptionManager != null) {
                    val subs = subscriptionManager.activeSubscriptionInfoList
                    if (subs != null) {
                        for (sub in subs) {
                            val number = sub.number
                            if (!number.isNullOrEmpty()) {
                                return number.replace("+91", "").replace(" ", "")
                            }
                        }
                    }
                }
            }

            // Fallback: TelephonyManager se
            val telephonyManager = getSystemService(TelephonyManager::class.java)
            val number = telephonyManager?.line1Number ?: ""
            number.replace("+91", "").replace(" ", "")
        } catch (e: Exception) {
            ""
        }
    }

    private fun askPhonePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_PHONE_STATE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_PHONE_STATE),
                    100
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val number = detectSimNumber()
                if (number.isNotEmpty()) {
                    val etPhone = findViewById<EditText>(R.id.etPhone)
                    etPhone.setText(number)
                    Toast.makeText(this, "SIM se number mila: $number", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this,
                        "SIM me number save nahi hai. Manually daalo.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
