package com.jodis.worldclock

import android.content.Context
import android.content.SharedPreferences

object FriendStore {

    private const val PREFS = "world_clock_friends"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun saveFriend(context: Context, username: String, displayName: String, phone: String) {
        prefs(context).edit()
            .putString(username, "$displayName|$phone")
            .apply()
    }

    fun getAllFriends(context: Context): List<Chat> {
        val all = prefs(context).all
        val list = mutableListOf<Chat>()
        for ((username, value) in all) {
            val parts = (value as? String)?.split("|")
            if (parts != null && parts.size == 2) {
                list.add(
                    Chat(
                        chatId = username,
                        displayName = parts[0],
                        phone = parts[1],
                        lastMessage = "Chat shuru karo",
                        lastTime = ""
                    )
                )
            }
        }
        return list
    }

    fun removeFriend(context: Context, username: String) {
        prefs(context).edit().remove(username).apply()
    }
}
