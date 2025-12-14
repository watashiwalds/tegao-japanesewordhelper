package com.tegaoteam.application.tegao.ui.homescreen.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.databinding.FragmentMainChatbotBinding

class ChatbotFragment: Fragment() {
    private lateinit var _binding: FragmentMainChatbotBinding
    private lateinit var _viewModel: ChatbotViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainChatbotBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(requireActivity())[ChatbotViewModel::class.java]

        initView()

        return _binding.root
    }

    private fun initView() {
        _binding.loChatBubbleBoxRcy.adapter = ChatBubbleListAdapter().apply { submitList(_viewModel.testChatBubble) }
        _binding.executePendingBindings()
    }
}