package com.jodis.worldclock

// RAM me messages aur chats rakhne ka system
// App band hone par sab clear ho jayega (Snapchat jaisa)
object MessageStore {

    val chats: MutableList<Chat> = mutableListOf()

    private val messagesMap: MutableMap<String, MutableList<Message>> = mutableMapOf()

    fun getMessages(chatId: String): MutableList<Message> {
        return messagesMap.getOrPut(chatId) { mutableListOf() }
    }

    fun addMessage(chatId: String, message: Message) {
        getMessages(chatId).add(message)
        val chat = chats.find { it.chatId == chatId }
        if (chat != null) {
            val index = chats.indexOf(chat)
            chats[index] = chat.copy(
                lastMessage = message.text,
                lastTime = formatTime(message.timestamp)
            )
        }
    }

    // Chat screen se bahar jate hi messages delete (Snapchat style)
    fun clearMessages(chatId: String) {
        messagesMap.remove(chatId)
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(java.util.Date(timestamp))
    }
}
