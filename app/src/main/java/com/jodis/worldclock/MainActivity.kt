package com.jodis.worldclock

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Dummy data — testing ke liye
        if (MessageStore.chats.isEmpty()) {
            MessageStore.chats.addAll(
                listOf(
                    Chat("rahul_123", "Rahul Sharma", "Bhai kal milte hain", "10:45", 2),
                    Chat("priya_07", "Priya Verma", "Okay done ✅", "09:20", 0),
                    Chat("amit_k", "Amit Kumar", "Photo bhej dena", "Yesterday", 1),
                    Chat("neha_x", "Neha Singh", "Good night 🌙", "Yesterday", 0),
                    Chat("vikas_99", "Vikas Yadav", "Call kar lena", "Monday", 0)
                )
            )
        }

        val chatList = findViewById<RecyclerView>(R.id.chatList)
        chatList.layoutManager = LinearLayoutManager(this)
        chatList.adapter = ChatAdapter(MessageStore.chats) { chat ->
            Toast.makeText(
                this,
                "Chat khulega: ${chat.displayName}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
