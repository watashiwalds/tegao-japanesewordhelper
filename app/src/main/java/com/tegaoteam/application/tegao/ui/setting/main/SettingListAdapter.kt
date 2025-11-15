package com.tegaoteam.application.tegao.ui.setting.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ItemListNavigationBinding
import com.tegaoteam.application.tegao.ui.component.generics.ListNavigationItemInfo

class SettingListAdapter: ListAdapter<ListNavigationItemInfo, SettingListAdapter.ViewHolder>(DiffCallback()) {
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
        holder.bind(getItem(position), navigatingFunction)
    }

    lateinit var navigatingFunction: (directionId: Int) -> Unit

    class ViewHolder private constructor (private val binding: ItemListNavigationBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(info: ListNavigationItemInfo, navFunc: (Int) -> Unit) {
            binding.listNavInfo = info
            binding.root.setOnClickListener { navFunc(info.directionId) }
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemListNavigationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<ListNavigationItemInfo>() {
        override fun areItemsTheSame(
            oldItem: ListNavigationItemInfo,
            newItem: ListNavigationItemInfo
        ): Boolean {
            return oldItem.labelResId == newItem.labelResId
        }

        override fun areContentsTheSame(
            oldItem: ListNavigationItemInfo,
            newItem: ListNavigationItemInfo
        ): Boolean {
            return oldItem == newItem
        }

    }
}