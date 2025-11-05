package com.tegaoteam.application.tegao.ui.component.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.tegaoteam.application.tegao.databinding.ItemTagBinding

class TagGroupListAdapter: ListAdapter<TagItem, TagGroupListAdapter.ViewHolder>(DiffCallback()) {
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

    /**
     * Unused, use submitRawTagList instead
     */
    override fun submitList(list: List<TagItem?>?) {}
    fun submitRawTagList(list: List<Pair<String, Any>>?) {
        var convertedList: MutableList<TagItem>? = null
        if (list != null) {
            convertedList = mutableListOf()
            for (tag in list) {
                val termKey = tag.first
                val tagInfo = tag.second
                when (tagInfo) {
                    is Pair<*, *> -> convertedList.add(TagItem.toTagItem(termKey, tagInfo.first as String, tagInfo.second as String))
                    else -> if (tagInfo.toString().isNotBlank()) convertedList.add(TagItem.toTagItem(termKey, tagInfo as String))
                }
            }
        }
            
//        convertedList = list?.filter { it.second?.isNotBlank()?: false }?.map { (termKey, label) -> TagItem.toTagItem(termKey, label?: "") }
        super.submitList(convertedList)
    }

    class ViewHolder private constructor(private val binding: ItemTagBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TagItem) {
            binding.infoTag = data
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<TagItem>() {
        override fun areItemsTheSame(
            oldItem: TagItem,
            newItem: TagItem
        ): Boolean {
            return oldItem.label == newItem.label
        }

        override fun areContentsTheSame(
            oldItem: TagItem,
            newItem: TagItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}