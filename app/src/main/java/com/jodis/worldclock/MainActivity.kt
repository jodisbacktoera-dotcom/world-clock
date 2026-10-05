package com.jodis.worldclock

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Abhi ke liye ek simple screen, baad me isme chat list aayegi
        val textView = TextView(this)
        textView.text = "Chat List Screen\n(Yahan chat list aayegi)"
        textView.textSize = 24f
        textView.setPadding(50, 50, 50, 50)
        setContentView(textView)
    }
}
