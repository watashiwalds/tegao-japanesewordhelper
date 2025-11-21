package com.tegaoteam.application.tegao.ui.component.themedchip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.BR
import com.tegaoteam.application.tegao.R

class ThemedChipGroupListAdapter<T: ViewDataBinding>(private val lifecycleOwner: LifecycleOwner, private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> T): ListAdapter<ThemedChipGroup, ThemedChipGroupListAdapter<T>.ViewHolder>( DiffCallback() ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ThemedChipGroupListAdapter<T>.ViewHolder {
        val v = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v, lifecycleOwner)
    }

    override fun onBindViewHolder(
        holder: ThemedChipGroupListAdapter<T>.ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: T, private val lifecycleOwner: LifecycleOwner): RecyclerView.ViewHolder(binding.root) {
        fun bind(group: ThemedChipGroup) {
            // bind manager to listAdapter first-hand to support my short-term memory
            group.listAdapter.themedChipManager = group.manager

            binding.setVariable(BR.groupInfo, group)
            binding.root.findViewById<RecyclerView>(R.id.themedChipDisplay_rcy).let {
                it.layoutManager = group.layoutManager
                it.adapter = group.listAdapter
            }
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<ThemedChipGroup>() {
        override fun areItemsTheSame(
            oldItem: ThemedChipGroup,
            newItem: ThemedChipGroup
        ): Boolean {
            return oldItem.manager == newItem.manager
        }

        override fun areContentsTheSame(
            oldItem: ThemedChipGroup,
            newItem: ThemedChipGroup
        ): Boolean {
            return oldItem == newItem
        }
    }
}