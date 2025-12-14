package com.tegaoteam.application.tegao.ui.homescreen.chatbot

import com.tegaoteam.application.tegao.data.database.chathistory.ChatLogEntity

data class ChatBubble(
    val id: Long,
    var sender: Int,
    var timestamp: String,
    var content: String
) {
    companion object {
        const val SENDER_USER = 0
        const val SENDER_BOT = 1

        fun fromChatLogEntity(entity: ChatLogEntity): Pair<ChatBubble, ChatBubble> = Pair(
            ChatBubble(
                id = entity.id,
                sender = SENDER_USER,
                timestamp = entity.sendAtTime,
                content = entity.sendText
            ),
            ChatBubble(
                id = entity.id,
                sender = SENDER_BOT,
                timestamp = entity.replyAtTime,
                content = entity.replyText
            )
        )
    }
}