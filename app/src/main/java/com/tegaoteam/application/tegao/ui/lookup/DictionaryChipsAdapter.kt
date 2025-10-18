package com.tegaoteam.application.tegao.ui.lookup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ThemedChipItemBinding
import com.tegaoteam.application.tegao.domain.model.Dictionary
import com.tegaoteam.application.tegao.utils.frontend.themedchip.ThemedChipController
import com.tegaoteam.application.tegao.utils.frontend.themedchip.ThemedChipItem

class DictionaryChipsAdapter(private val lifecycleOwner: LifecycleOwner): ListAdapter<ThemedChipItem, DictionaryChipsAdapter.ViewHolder>( DiffCallback() ) {
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

    //Convert DictionaryConfig.Dict list to ThemedChipItem list
    /**
     * Please use [submitDictList] instead of this
     */
    override fun submitList(list: List<ThemedChipItem?>?) {} //emptied the function to swap with DictionaryConfig.Dict submitDictList()
    private val chipItems = mutableListOf<ThemedChipItem>()
    //Chip control design for 1 selectable chip in concurrent
    private lateinit var themedChipController: ThemedChipController

    /**
     * Exclusively design for DictionaryChipAdapter
     *
     * @param list Typical list of available dict fetch from config
     * @param clickListener Lambda with ```dictId``` as parameter to run per-dict function
     */
    fun submitDictList(list: List<Dictionary?>?, clickListener: (id: String) -> Unit) {
        chipItems.clear()
        themedChipController = ThemedChipController(chipItems as List<ThemedChipItem>, ThemedChipController.MODE_SINGLE)
        list?.forEach { item ->
            item?.let {
                val newChip = ThemedChipItem(
                    it.id,
                    it.displayName,
                    MutableLiveData<Boolean>()
                )
                newChip.setOnClickListener { clickListener(newChip.id); themedChipController.setSelected(newChip) }
                chipItems.add(newChip)
            }
        }
        super.submitList(chipItems)
        themedChipController.selectFirst()
    }

    class ViewHolder private constructor (private val binding: ThemedChipItemBinding, private val lifecycleOwner: LifecycleOwner): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ThemedChipItem) {
            binding.itemChip = item
            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup, lifecycleOwner: LifecycleOwner): ViewHolder {
                val binding = ThemedChipItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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