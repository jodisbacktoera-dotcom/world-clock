package com.jodis.worldclock

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val SMS_ROLE_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val headerName = findViewById<TextView>(R.id.headerName)
        headerName.text = UserData.getDisplayName(this)

        // Dummy data testing ke liye (phone number apna daal sakte ho)
        if (MessageStore.chats.isEmpty()) {
            MessageStore.chats.addAll(
                listOf(
                    Chat("rahul_123", "Rahul Sharma", "9876543210", "Bhai kal milte hain", "10:45", 2),
                    Chat("priya_07", "Priya Verma", "9876543211", "Okay done", "09:20", 0),
                    Chat("amit_k", "Amit Kumar", "9876543212", "Photo bhej dena", "Yesterday", 1)
                )
            )
        }

        val chatList = findViewById<RecyclerView>(R.id.chatList)
        chatList.layoutManager = LinearLayoutManager(this)
        chatList.adapter = ChatAdapter(MessageStore.chats) { chat ->
            // Chat par tap → ChatActivity kholo
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatId", chat.chatId)
            intent.putExtra("chatName", chat.displayName)
            intent.putExtra("chatPhone", chat.phone)
            startActivity(intent)
        }

        val smsBanner = findViewById<LinearLayout>(R.id.smsBanner)
        smsBanner.setOnClickListener { requestSmsRole() }

        updateBanner()
    }

    override fun onResume() {
        super.onResume()
        updateBanner()
    }

    private fun updateBanner() {
        val smsBanner = findViewById<LinearLayout>(R.id.smsBanner)
        if (isDefaultSmsApp()) {
            smsBanner.visibility = LinearLayout.GONE
        } else {
            smsBanner.visibility = LinearLayout.VISIBLE
        }
    }

    private fun isDefaultSmsApp(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            roleManager?.isRoleHeld(RoleManager.ROLE_SMS) == true
        } else {
            Telephony.Sms.getDefaultSmsPackage(this) == packageName
        }
    }

    private fun requestSmsRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                    val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
                    startActivityForResult(intent, SMS_ROLE_CODE)
                }
            }
        } else {
            val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SMS_ROLE_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Chat enable ho gaya! ✅", Toast.LENGTH_SHORT).show()
                updateBanner()
            } else {
                Toast.makeText(this, "Permission deny kiya", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
