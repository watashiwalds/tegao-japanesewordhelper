package com.tegaoteam.application.tegao.ui.homescreen.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.data.hub.OnlineServiceHub
import com.tegaoteam.application.tegao.databinding.FragmentMainChatbotBinding

class ChatbotFragment: Fragment() {
    private lateinit var _binding: FragmentMainChatbotBinding
    private lateinit var _viewModel: ChatbotViewModel
    private lateinit var _adapter: ChatBubbleListAdapter
    private val _onlineServiceHub = OnlineServiceHub()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainChatbotBinding.inflate(inflater, container, false)
        _viewModel = ViewModelProvider(requireActivity(), ChatbotViewModel.Companion.ViewModelFactory(_onlineServiceHub))[ChatbotViewModel::class.java]
        _adapter = ChatBubbleListAdapter()

        initObservers()
        initView()

        return _binding.root
    }

    private fun initObservers() {
        _viewModel.apply {
            recentChats.observe(viewLifecycleOwner) {
                _adapter.submitList(it)
            }
        }
    }

    private fun initView() {
        _binding.loChatBubbleBoxRcy.adapter = _adapter
        _binding.executePendingBindings()
    }
}