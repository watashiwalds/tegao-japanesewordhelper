package com.tegaoteam.application.tegao.ui.homescreen.chatbot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.tegaoteam.application.tegao.data.hub.OnlineServiceHub
import com.tegaoteam.application.tegao.data.model.asFlow
import kotlin.math.log

class ChatbotViewModel(private val _onlineServiceHub: OnlineServiceHub): ViewModel() {
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

    val recentChats = _onlineServiceHub.getRecentChat().asFlow().asLiveData().map { it.map { log -> ChatBubble.fromChatLogEntity(log).toList().reversed() }.flatten() }
    private val _currentChat = MutableLiveData<Pair<ChatBubble, ChatBubble>>()
    val currentChat: LiveData<Pair<ChatBubble, ChatBubble>> = _currentChat
    fun sendQuestion(questionText: String) {

    }

    companion object {
        class ViewModelFactory(
            private val onlineServiceHub: OnlineServiceHub
        ) : ViewModelProvider.Factory {
            @Suppress("unchecked_cast")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ChatbotViewModel::class.java)) {
                    return ChatbotViewModel(onlineServiceHub) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}