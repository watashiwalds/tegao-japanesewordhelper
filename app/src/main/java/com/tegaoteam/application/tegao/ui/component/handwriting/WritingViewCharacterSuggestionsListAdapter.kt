package com.tegaoteam.application.tegao.ui.component.handwriting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ItemWritingboardCharacterSuggestBinding
import com.tegaoteam.application.tegao.ui.component.handwriting.WritingViewCharacterSuggestionsListAdapter.ViewHodler

class WritingViewCharacterSuggestionsListAdapter(private val onSuggestionSelected: (String) -> Unit): ListAdapter<String, ViewHodler>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHodler {
        return ViewHodler.from(parent)
    }

    override fun onBindViewHolder(
        holder: ViewHodler,
        position: Int
    ) {
        holder.bind(getItem(position), onSuggestionSelected)
    }

    class ViewHodler private constructor(private val binding: ItemWritingboardCharacterSuggestBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(value: String, onSuggestionSelected: (String) -> Unit) {
            binding.suggestion.text = value
            binding.root.setOnClickListener { onSuggestionSelected.invoke(value) }
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHodler {
                val binding = ItemWritingboardCharacterSuggestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHodler(binding)
            }
        }
    }

    class DiffCallback(): DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}