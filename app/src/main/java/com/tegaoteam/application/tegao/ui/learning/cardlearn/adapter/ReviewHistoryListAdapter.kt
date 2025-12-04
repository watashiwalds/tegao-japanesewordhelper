package com.tegaoteam.application.tegao.ui.learning.cardlearn.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ItemCardLearnReviewedQuickinfoBinding
import com.tegaoteam.application.tegao.ui.learning.cardlearn.model.ReviewHistoryInfo

class ReviewHistoryListAdapter: ListAdapter<ReviewHistoryInfo, ReviewHistoryListAdapter.ViewHolder>(DiffCallback()) {
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

    class ViewHolder private constructor(private val binding: ItemCardLearnReviewedQuickinfoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReviewHistoryInfo) {
            binding.dataInfo = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val b = ItemCardLearnReviewedQuickinfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(b)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<ReviewHistoryInfo>() {
        override fun areItemsTheSame(
            oldItem: ReviewHistoryInfo,
            newItem: ReviewHistoryInfo
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ReviewHistoryInfo,
            newItem: ReviewHistoryInfo
        ): Boolean {
            return oldItem == newItem
        }

    }
}