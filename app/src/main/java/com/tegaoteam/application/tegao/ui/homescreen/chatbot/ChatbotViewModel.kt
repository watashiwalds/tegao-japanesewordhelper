package com.tegaoteam.application.tegao.ui.homescreen.chatbot

import androidx.lifecycle.ViewModel

class ChatbotViewModel: ViewModel() {
    val testChatBubble = mutableListOf(
        ChatBubble(
            id = -1,
            sender = ChatBubble.SENDER_USER,
            timestamp = "LOL 1234",
            content = "Is this a real chatbot? Can I chat with you for real?"
        ),
        ChatBubble(
            id = -1,
            sender = ChatBubble.SENDER_BOT,
            timestamp = "LOL 1234",
            content = "It would be, yes. But for now, this fragment can only display this exactly conversation for display test purpose."
        ),
        ChatBubble(
            id = -1,
            sender = ChatBubble.SENDER_USER,
            timestamp = "LOL 1234",
            content = "So... Can you display a few thing extra? Like example and stuff?"
        ),
        ChatBubble(
            id = -1,
            sender = ChatBubble.SENDER_BOT,
            timestamp = "LOL 1234",
            content = "Extra in terms of multiple line? Yes. Special display design? As of now, no.\nLike this, is a\nnew\nline"
        ),
        ChatBubble(
            id = -1,
            sender = ChatBubble.SENDER_USER,
            timestamp = "LOL 1234",
            content = "Oh... ok. Thinking of an answer please."
        ),
        ChatBubble(
            id = -1,
            sender = ChatBubble.SENDER_BOT,
            timestamp = "",
            content = "Thinking..."
        )
    ).apply { reverse() }
}