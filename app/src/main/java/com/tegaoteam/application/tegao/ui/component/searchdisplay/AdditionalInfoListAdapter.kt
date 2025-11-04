package com.tegaoteam.application.tegao.ui.component.searchdisplay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ItemDefinitionAdditionalInfoBinding
import com.tegaoteam.application.tegao.ui.component.tag.TagItem

class AdditionalInfoListAdapter: ListAdapter<Pair<String, String>, AdditionalInfoListAdapter.ViewHolder>(DiffCallback()) {
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

    class ViewHolder private constructor(private val binding: ItemDefinitionAdditionalInfoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Pair<String, String>) {
            binding.expandTag.infoTag = TagItem.toTagItem(data.first, data.first)
            binding.expandContent.text = data.second
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemDefinitionAdditionalInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Pair<String, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>
        ): Boolean {
            return oldItem.second == newItem.second
        }

        override fun areContentsTheSame(
            oldItem: Pair<String, String>,
            newItem: Pair<String, String>
        ): Boolean {
            return oldItem == newItem
        }

    }
}