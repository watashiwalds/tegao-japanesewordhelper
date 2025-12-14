package com.tegaoteam.application.tegao.ui.homescreen.chatbot

import com.tegaoteam.application.tegao.data.database.chathistory.ChatLogEntity
import com.tegaoteam.application.tegao.utils.Time

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

        fun toChatLogEntity(converse: Pair<ChatBubble, ChatBubble>) = ChatLogEntity(
            id = 0,
            sendAtTime = converse.first.timestamp,
            sendText = converse.first.content,
            replyAtTime = converse.second.timestamp,
            replyText = converse.second.content
        )
    }
}