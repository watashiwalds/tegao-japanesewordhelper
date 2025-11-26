package com.tegaoteam.application.tegao.ui.learning.cardmanage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ItemCardManageInfoCrudqabBinding
import com.tegaoteam.application.tegao.ui.homescreen.learning.LearningInfoDataClasses

class QuickCrudItemListAdapter: ListAdapter<LearningInfoDataClasses.QuickCrudItemInfo, QuickCrudItemListAdapter.ViewHolder>(DiffCallback()) {
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

    class ViewHolder private constructor(private val binding: ItemCardManageInfoCrudqabBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LearningInfoDataClasses.QuickCrudItemInfo) {
            binding.dataInfo = item
            binding.lifecycleOwner = item.lifecycleOwner
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemCardManageInfoCrudqabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<LearningInfoDataClasses.QuickCrudItemInfo>() {
        override fun areItemsTheSame(
            oldItem: LearningInfoDataClasses.QuickCrudItemInfo,
            newItem: LearningInfoDataClasses.QuickCrudItemInfo
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: LearningInfoDataClasses.QuickCrudItemInfo,
            newItem: LearningInfoDataClasses.QuickCrudItemInfo
        ): Boolean {
            return oldItem == newItem
        }

    }
}