package com.jodis.worldclock

data class Chat(
    val chatId: String,
    val displayName: String,
    val lastMessage: String,
    val lastTime: String,
    val unreadCount: Int = 0
)
