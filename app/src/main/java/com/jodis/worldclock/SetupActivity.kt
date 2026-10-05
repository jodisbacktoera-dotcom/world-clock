package com.jodis.worldclock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SetupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etDisplayName = findViewById<EditText>(R.id.etDisplayName)
        val etPin = findViewById<EditText>(R.id.etPin)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val displayName = etDisplayName.text.toString().trim()
            val pin = etPin.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            // Validation
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

            // Save karo
            UserData.saveUser(this, username, displayName, pin, phone)

            Toast.makeText(this, "Setup complete! ✅", Toast.LENGTH_SHORT).show()

            // MainActivity kholo
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
