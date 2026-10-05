package com.jodis.worldclock

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PinLockActivity : AppCompatActivity() {

    private var enteredPin = ""
    private lateinit var dotsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_lock)

        window.setFlags(
            android.view.WindowManager.LayoutParams.FLAG_SECURE,
            android.view.WindowManager.LayoutParams.FLAG_SECURE
        )

        dotsText = findViewById(R.id.dotsText)

        val buttons = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )
        for (id in buttons) {
            findViewById<Button>(id).setOnClickListener {
                addDigit((it as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.btnClear).setOnClickListener {
            enteredPin = ""
            updateDots()
        }
    }

    private fun addDigit(digit: String) {
        if (enteredPin.length < 4) {
            enteredPin += digit
            updateDots()
            if (enteredPin.length == 4) {
                verifyPin()
            }
        }
    }

    private fun updateDots() {
        val dots = "● ".repeat(enteredPin.length) + "○ ".repeat(4 - enteredPin.length)
        dotsText.text = dots.trim()
    }

    private fun verifyPin() {
        // Default PIN abhi 1234 hai
        if (enteredPin == "1234") {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "Galat PIN", Toast.LENGTH_SHORT).show()
            enteredPin = ""
            updateDots()
        }
    }

    override fun onBackPressed() {
        startActivity(Intent(this, ClockActivity::class.java))
        finish()
    }
}
