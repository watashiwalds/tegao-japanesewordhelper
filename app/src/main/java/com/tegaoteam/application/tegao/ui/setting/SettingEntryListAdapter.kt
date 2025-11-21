package com.tegaoteam.application.tegao.ui.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ItemSettingConfigDisplayBinding
import com.tegaoteam.application.tegao.databinding.SubitemSettingConfigBtnBooleanBinding
import com.tegaoteam.application.tegao.ui.setting.model.ConfigEntryItem
import com.tegaoteam.application.tegao.utils.preset.DialogPreset

@Suppress("unchecked_cast")
class SettingEntryListAdapter(private val lifecyclerOwner: LifecycleOwner): ListAdapter<ConfigEntryItem, SettingEntryListAdapter.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder.from(parent, lifecyclerOwner)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class ViewHolder private constructor (private val binding: ItemSettingConfigDisplayBinding, private val lifecycleOwner: LifecycleOwner): RecyclerView.ViewHolder(binding.root) {
        fun bind(info: ConfigEntryItem) {
            val lcO = lifecycleOwner
            when (info.type) {
                ConfigEntryItem.Companion.Type.BOOLEAN -> binding.loLateinitSettingFrm.apply {
                    removeAllViews()
                    val subBinding = SubitemSettingConfigBtnBooleanBinding.inflate(LayoutInflater.from(context), this, false).apply {
                        liveData = info.liveData as LiveData<Boolean>?
                        lifecycleOwner = lcO
                        executePendingBindings()
                    }
                    addView(subBinding.root)
                }
                ConfigEntryItem.Companion.Type.CONFIRMATION -> {
                    val original = info.clickListener
                    info.clickListener = {
                        DialogPreset.requestConfirmation(
                            binding.root.context,
                            info.labelResId,
                            info.descriptionResId,
                            original
                        )
                    }
                }
                ConfigEntryItem.Companion.Type.DECORATIVE_LABEL -> binding.label.apply {
                    background = AppCompatResources.getDrawable(context, R.drawable.neutral_stroke_end)
                }
                ConfigEntryItem.Companion.Type.PENDING_INTENT -> {}
            }
            binding.info = info
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup, lifecyclerOwner: LifecycleOwner): ViewHolder {
                val binding = ItemSettingConfigDisplayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding, lifecyclerOwner)
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