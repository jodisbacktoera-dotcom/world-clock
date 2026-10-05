package com.jodis.worldclock

import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatActivity : AppCompatActivity() {

    private lateinit var chatId: String
    private lateinit var chatName: String
    private lateinit var chatPhone: String
    private lateinit var adapter: MessageAdapter
    private lateinit var messageList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        // Screenshot block
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        chatId = intent.getStringExtra("chatId") ?: ""
        chatName = intent.getStringExtra("chatName") ?: "Chat"
        chatPhone = intent.getStringExtra("chatPhone") ?: ""

        // Header me naam daalo
        findViewById<TextView>(R.id.chatTitle).text = chatName

        // Back button
        findViewById<TextView>(R.id.backBtn).setOnClickListener {
            finish()
        }

        // Message list setup
        messageList = findViewById(R.id.messageList)
        messageList.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        adapter = MessageAdapter(MessageStore.getMessages(chatId))
        messageList.adapter = adapter

        // Send button
        val input = findViewById<EditText>(R.id.messageInput)
        findViewById<TextView>(R.id.sendBtn).setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener
            if (chatPhone.isEmpty()) {
                Toast.makeText(this, "Phone number missing", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // SMS bhejo
            SmsSender.sendMessage(chatPhone, text)

            // Screen par dikhao
            val msg = Message(
                senderId = "me",
                text = text,
                timestamp = System.currentTimeMillis(),
                isMine = true
            )
            MessageStore.addMessage(chatId, msg)
            adapter.notifyItemInserted(MessageStore.getMessages(chatId).size - 1)
            messageList.scrollToPosition(MessageStore.getMessages(chatId).size - 1)
            input.text.clear()
        }
    }

    override fun onStop() {
        super.onStop()
        // Snapchat style: chat se bahar jate hi messages delete
        MessageStore.clearMessages(chatId)
    }
}
