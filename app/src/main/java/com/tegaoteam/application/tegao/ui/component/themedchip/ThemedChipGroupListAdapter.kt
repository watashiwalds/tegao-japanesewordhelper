package com.tegaoteam.application.tegao.ui.component.themedchip

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
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
            binding.setVariable(BR.groupInfo, group)
            binding.root.findViewById<RecyclerView>(R.id.unv_themedChipDisplay_rcy)?.let {
                it.layoutManager = group.layoutManager
                it.adapter = group.listAdapter
            }
            if (group.allowQuickSelect) binding.root.findViewById<CheckBox>(R.id.lo_selectAllCue_ckx)?.let {
                it.setOnClickListener { group.qabToggleSelectAll() }
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