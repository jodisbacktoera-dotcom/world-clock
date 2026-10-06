package com.jodis.worldclock

import android.app.AlertDialog
import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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

        findViewById<TextView>(R.id.btnMyQr).setOnClickListener {
            startActivity(Intent(this, MyQrActivity::class.java))
        }

        findViewById<TextView>(R.id.btnAddFriend).setOnClickListener {
            startActivity(Intent(this, AddFriendActivity::class.java))
        }

        val smsBanner = findViewById<LinearLayout>(R.id.smsBanner)
        smsBanner.setOnClickListener { showDefaultAppDialog() }

        updateBanner()

        // App kholte hi dialog dikhao agar default SMS app nahi hai
        if (!isDefaultSmsApp()) {
            showDefaultAppDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        updateBanner()
        loadChats()
    }

    private fun loadChats() {
        val friends = FriendStore.getAllFriends(this)

        if (friends.isEmpty() && MessageStore.chats.isEmpty()) {
            MessageStore.chats.addAll(
                listOf(
                    Chat("rahul_123", "Rahul Sharma", "9876543210", "Bhai kal milte hain", "10:45", 2),
                    Chat("priya_07", "Priya Verma", "9876543211", "Okay done", "09:20", 0),
                    Chat("amit_k", "Amit Kumar", "9876543212", "Photo bhej dena", "Yesterday", 1)
                )
            )
        } else if (friends.isNotEmpty()) {
            MessageStore.chats.clear()
            MessageStore.chats.addAll(friends)
        }

        val chatList = findViewById<RecyclerView>(R.id.chatList)
        chatList.layoutManager = LinearLayoutManager(this)
        chatList.adapter = ChatAdapter(MessageStore.chats) { chat ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatId", chat.chatId)
            intent.putExtra("chatName", chat.displayName)
            intent.putExtra("chatPhone", chat.phone)
            startActivity(intent)
        }
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

    // Truecaller jaisa popup
    private fun showDefaultAppDialog() {
        if (isDefaultSmsApp()) return

        AlertDialog.Builder(this)
            .setTitle("SMS Permission Chahiye")
            .setMessage("World Clock ko default SMS app banana padega taaki aapke messages yahin aayein, Google Messages me na jayein. Settings kholu?")
            .setCancelable(false)
            .setPositiveButton("Settings Kholo") { _, _ ->
                requestSmsRole()
            }
            .setNegativeButton("Baad Me") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun requestSmsRole() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(RoleManager::class.java)
            if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
                if (!roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                    try {
                        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_SMS)
                        startActivityForResult(intent, SMS_ROLE_CODE)
                    } catch (e: Exception) {
                        openDefaultAppsSettings()
                    }
                }
            } else {
                openDefaultAppsSettings()
            }
        } else {
            try {
                val intent = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
                intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
                startActivity(intent)
            } catch (e: Exception) {
                openDefaultAppsSettings()
            }
        }
    }

    private fun openDefaultAppsSettings() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
            startActivity(intent)
            Toast.makeText(this, "SMS app me World Clock select karo", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (e2: Exception) {
                Toast.makeText(this, "Settings → Apps → Default apps → SMS", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SMS_ROLE_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Chat enable ho gaya! ✅", Toast.LENGTH_SHORT).show()
                updateBanner()
            } else {
                Toast.makeText(this, "Permission zaroori hai chat ke liye", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
