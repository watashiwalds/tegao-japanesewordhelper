package com.tegaoteam.application.tegao.ui.component.themedchip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ItemThemedChipBinding

class ThemedChipListAdapter(private val lifecycleOwner: LifecycleOwner): ListAdapter<ThemedChipItem, ThemedChipListAdapter.ViewHolder>( DiffCallback() ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent, lifecycleOwner)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    //Manage ThemedChipItem list
    //Chip control design for 1 selectable chip in concurrent
    private lateinit var themedChipController: ThemedChipController

    /**
     * Unused, use submitListWithClickListener instead
     */
    override fun submitList(list: List<ThemedChipItem>?) {}
    fun submitListWithClickListener(list: List<ThemedChipItem>?, listener: (id: String) -> Unit) {
        themedChipController = ThemedChipController(list?: listOf(), ThemedChipController.MODE_SINGLE)
        list?.forEach { it.setOnClickListener { listener(it.id); themedChipController.setSelected(it) } }
        super.submitList(list)
        themedChipController.selectFirst()
    }

    class ViewHolder private constructor (private val binding: ItemThemedChipBinding, private val lifecycleOwner: LifecycleOwner): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ThemedChipItem) {
            binding.itemChip = item
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup, lifecycleOwner: LifecycleOwner): ViewHolder {
                val binding = ItemThemedChipBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding, lifecycleOwner)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<ThemedChipItem>() {
        override fun areItemsTheSame(
            oldItem: ThemedChipItem,
            newItem: ThemedChipItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ThemedChipItem,
            newItem: ThemedChipItem
        ): Boolean {
            return oldItem.label == newItem.label && oldItem.isSelected.value == newItem.isSelected.value
        }

    }
}