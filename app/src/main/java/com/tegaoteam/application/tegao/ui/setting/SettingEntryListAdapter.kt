package com.tegaoteam.application.tegao.ui.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.ItemSettingConfigDisplayBinding
import com.tegaoteam.application.tegao.databinding.SubitemSettingConfigBtnBooleanBinding
import com.tegaoteam.application.tegao.ui.setting.model.ConfigEntryItem
import com.tegaoteam.application.tegao.ui.setting.model.ConfigType

@Suppress("unchecked_cast")
class SettingEntryListAdapter: ListAdapter<ConfigEntryItem, SettingEntryListAdapter.ViewHolder>(DiffCallback()) {
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

    class ViewHolder private constructor (private val binding: ItemSettingConfigDisplayBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(info: ConfigEntryItem) {
            binding.info = info
            when (info.type) {
                ConfigType.BOOLEAN -> binding.loLateinitSettingFrm.apply {
                    removeAllViews()
                    val subBinding = SubitemSettingConfigBtnBooleanBinding.inflate(LayoutInflater.from(context), this, false).apply {
                        liveData = info.liveData as LiveData<Boolean>
                        executePendingBindings()
                    }
                    addView(subBinding.root)
                }
            }
            binding.root.setOnClickListener { info.clickListener }
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemSettingConfigDisplayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<ConfigEntryItem>() {
        override fun areItemsTheSame(
            oldItem: ConfigEntryItem,
            newItem: ConfigEntryItem
        ): Boolean {
            return oldItem.labelResId == newItem.labelResId
        }

        override fun areContentsTheSame(
            oldItem: ConfigEntryItem,
            newItem: ConfigEntryItem
        ): Boolean {
            return oldItem == newItem
        }

    }
}