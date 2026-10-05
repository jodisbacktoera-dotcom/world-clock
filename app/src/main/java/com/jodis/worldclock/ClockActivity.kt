package com.jodis.worldclock

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ClockActivity : AppCompatActivity() {

    private var tapCount = 0
    private var lastTapTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clock)

        val timeText = findViewById<TextView>(R.id.timeText)
        val dateText = findViewById<TextView>(R.id.dateText)
        val hiddenArea = findViewById<View>(R.id.hiddenArea)

        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                val now = Calendar.getInstance()
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val dateFormat = SimpleDateFormat("EEEE, d MMM", Locale.getDefault())
                timeText.text = timeFormat.format(now.time)
                dateText.text = dateFormat.format(now.time)
                handler.postDelayed(this, 1000)
            }
        })

        hiddenArea.setOnClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTapTime > 500) {
                tapCount = 0
            }
            tapCount++
            lastTapTime = currentTime

            if (tapCount >= 3) {
                tapCount = 0
                startActivity(Intent(this, PinLockActivity::class.java))
            }
        }
    }

    override fun onBackPressed() {
        finishAffinity()
    }
}
