package com.jodis.worldclock

data class Message(
    val senderId: String,
    val text: String,
    val timestamp: Long,
    val isMine: Boolean
)
