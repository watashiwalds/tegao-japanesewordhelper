package com.tegaoteam.application.tegao.ui.component.themedchip

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.BR
import timber.log.Timber

/**
 *  Generic ListAdapter for every kind of ThemedChipItem chip style
 *
 *  @param lifecycleOwner Lifecycle owner for controlling chips state
 *  @param bindingInflater ::inflater of the ViewDataBinding class linked to the desired-to-use chip XML (need to have itemChip variable in order to work)
 */
class SingleSelectThemedChipListAdapter<T: ViewDataBinding>(private val lifecycleOwner: LifecycleOwner, private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> T): ListAdapter<ThemedChipItem, SingleSelectThemedChipListAdapter<T>.ViewHolder>( DiffCallback() ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val v = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v, lifecycleOwner)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    //Manage ThemedChipItem list
    //Chip control design for 1 selectable chip in concurrent
    var themedChipManager: ThemedChipManager? = null
        private set

    /**
     * Unused, use submitListWithClickListener instead
     */
    override fun submitList(list: List<ThemedChipItem>?) {}

    /**
     * Pass in a <ThemedChipItem> list with clickListener for wanted picking behavior
     */
    fun submitListWithClickListener(list: List<ThemedChipItem>?, listener: (id: String) -> Unit) {
        themedChipManager = ThemedChipManager(list?: listOf(), ThemedChipManager.MODE_SINGLE)
        list?.forEach { it.onSelectedListener = { listener(it.id) } }
        super.submitList(list)
    }

    inner class ViewHolder (private val binding: T, private val lifecycleOwner: LifecycleOwner): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ThemedChipItem) {
            binding.setVariable(BR.itemChip, item)
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
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