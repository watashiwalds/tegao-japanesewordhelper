package com.tegaoteam.application.tegao.ui.homescreen.chatbot

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ItemChatboxBubbleBinding
import com.tegaoteam.application.tegao.utils.Time

class ChatBubbleListAdapter: ListAdapter<ChatBubble, ChatBubbleListAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor(private val binding: ItemChatboxBubbleBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatBubble) {
            binding.bubble = item.copy().apply { timestamp = Time.formatToPrettyString(timestamp) }
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemChatboxBubbleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }
    class DiffCallback: DiffUtil.ItemCallback<ChatBubble>() {
        override fun areItemsTheSame(
            oldItem: ChatBubble,
            newItem: ChatBubble
        ): Boolean {
            return oldItem.id == newItem.id && oldItem.sender == newItem.sender
        }

        override fun areContentsTheSame(
            oldItem: ChatBubble,
            newItem: ChatBubble
        ): Boolean {
            return oldItem == newItem
        }

    }
}