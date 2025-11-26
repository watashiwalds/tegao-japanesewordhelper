package com.tegaoteam.application.tegao.ui.homescreen.learning

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ItemLearningDashboardCardGroupBinding

class DashboardGroupListAdapter: ListAdapter<LearningInfoDataClasses.DashboardCardGroupInfo, DashboardGroupListAdapter.ViewHolder> (DiffCallback()) {
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

    class ViewHolder private constructor(private val binding: ItemLearningDashboardCardGroupBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(dataInfo: LearningInfoDataClasses.DashboardCardGroupInfo) {
            binding.dataInfo = dataInfo
            binding.lifecycleOwner = dataInfo.lifecycleOwner
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemLearningDashboardCardGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }
    class DiffCallback: DiffUtil.ItemCallback<LearningInfoDataClasses.DashboardCardGroupInfo>() {
        override fun areItemsTheSame(
            oldItem: LearningInfoDataClasses.DashboardCardGroupInfo,
            newItem: LearningInfoDataClasses.DashboardCardGroupInfo
        ): Boolean {
            return oldItem.groupEntry == newItem.groupEntry
        }
        override fun areContentsTheSame(
            oldItem: LearningInfoDataClasses.DashboardCardGroupInfo,
            newItem: LearningInfoDataClasses.DashboardCardGroupInfo
        ): Boolean {
            return oldItem == newItem
        }

    }
}