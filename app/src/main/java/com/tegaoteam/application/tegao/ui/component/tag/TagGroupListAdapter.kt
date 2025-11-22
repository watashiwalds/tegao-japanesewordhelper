package com.tegaoteam.application.tegao.ui.component.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.tegaoteam.application.tegao.BR

class TagGroupListAdapter<T: ViewDataBinding>(private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> T): ListAdapter<TagItem, TagGroupListAdapter<T>.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

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
        super.submitList(convertedList)
    }

    inner class ViewHolder(private val binding: T): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TagItem) {
            binding.setVariable(BR.infoTag, data)
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