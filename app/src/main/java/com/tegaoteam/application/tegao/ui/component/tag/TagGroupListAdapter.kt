package com.tegaoteam.application.tegao.ui.component.tag

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.databinding.ItemTagBinding
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.TermBank

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
     * Unused, use
     */
    override fun submitList(list: List<TagItem?>?) {}
    fun submitRawTagList(list: List<Pair<String, String?>>?) {
        val convertedList = list?.map { (id, label) -> TagItem(
            label = label?: "",
            color = ContextCompat.getColor(TegaoApplication.instance.applicationContext, R.color.neutral),
            detail = TermBank.getTerm(id),
            clickListener = { tagItem -> AppToast.show(TegaoApplication.instance.applicationContext, tagItem.detail.toString(), AppToast.LENGTH_SHORT)}
        ) }
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