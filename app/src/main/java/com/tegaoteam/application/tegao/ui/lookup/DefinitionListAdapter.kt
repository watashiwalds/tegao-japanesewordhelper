package com.tegaoteam.application.tegao.ui.lookup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ItemDefinitionBinding
import com.tegaoteam.application.tegao.domain.model.Word

class DefinitionListAdapter: ListAdapter<Word.Definition, DefinitionListAdapter.ViewHolder>(DiffCallback()) {
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

    class ViewHolder private constructor(private val binding: ItemDefinitionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(defy: Word.Definition) {
            //TODO
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemDefinitionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Word.Definition>() {
        override fun areItemsTheSame(
            oldItem: Word.Definition,
            newItem: Word.Definition
        ): Boolean {
            return oldItem.meaning == newItem.meaning
        }

        override fun areContentsTheSame(
            oldItem: Word.Definition,
            newItem: Word.Definition
        ): Boolean {
            return oldItem == newItem
        }

    }
}