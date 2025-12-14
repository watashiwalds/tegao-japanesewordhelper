package com.tegaoteam.application.tegao.ui.homescreen.chatbot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.OnlineServiceHub
import com.tegaoteam.application.tegao.data.model.asFlow
import com.tegaoteam.application.tegao.domain.independency.RepoResult
import com.tegaoteam.application.tegao.ui.account.SignInHelper
import com.tegaoteam.application.tegao.utils.Time
import com.tegaoteam.application.tegao.utils.getStringFromAppRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.log

class ChatbotViewModel(private val _onlineServiceHub: OnlineServiceHub): ViewModel() {
    val recentChats = _onlineServiceHub.getRecentChat().asFlow().asLiveData().map { it.map { log -> ChatBubble.fromChatLogEntity(log).toList().reversed() }.flatten() }
    private val _currentChat = MutableLiveData<Pair<ChatBubble, ChatBubble>>()
    val currentChat: LiveData<Pair<ChatBubble, ChatBubble>> = _currentChat
    fun sendQuestion(questionText: String) {
        _currentChat.value = Pair(
            ChatBubble(
                id = 0,
                sender = ChatBubble.SENDER_USER,
                timestamp = Time.getCurrentTimestamp().toString(),
                content = questionText
            ),
            ChatBubble(
                id = 0,
                sender = ChatBubble.SENDER_BOT,
                timestamp = "",
                content = getStringFromAppRes(R.string.chatbot_botAnswerThinking)
            )
        )
        Timber.d("Request answer from chat for $questionText")

        SignInHelper.getUserToken { userToken ->
            Timber.d("Token received to send chat $userToken")
            viewModelScope.launch(Dispatchers.IO) {
                val res = _onlineServiceHub.sendQuestionToChatbot(userToken?: "", questionText)
                withContext(Dispatchers.Main) {
                    when (res) {
                        is RepoResult.Error<*> -> {
                            Timber.d("Chat return error")
                            _currentChat.value = Pair(
                                _currentChat.value!!.first,
                                ChatBubble(
                                    id = 0,
                                    sender = ChatBubble.SENDER_BOT,
                                    timestamp = "",
                                    content = "${res.code} ${res.message}"
                                )
                            )
                        }
                        is RepoResult.Success<String> -> {
                            Timber.d("Chat return answer")
                            _currentChat.value = Pair(
                                _currentChat.value!!.first,
                                ChatBubble(
                                    id = 0,
                                    sender = ChatBubble.SENDER_BOT,
                                    timestamp = Time.getCurrentTimestamp().toString(),
                                    content = res.data
                                )
                            )
                            viewModelScope.launch(Dispatchers.IO) {
                                _onlineServiceHub.upsertChat(ChatBubble.toChatLogEntity(_currentChat.value!!))
                            }
                        }
                    }
                }
            }
        }
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