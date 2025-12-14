package com.tegaoteam.application.tegao.ui.homescreen.chatbot

data class ChatBubble(
    val id: Long,
    val sender: Int,
    val timestamp: String,
    val content: String
) {
    companion object {
        const val SENDER_USER = 0
        const val SENDER_BOT = 1
    }
}