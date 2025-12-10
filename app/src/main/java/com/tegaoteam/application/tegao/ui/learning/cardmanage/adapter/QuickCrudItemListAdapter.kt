package com.tegaoteam.application.tegao.ui.learning.cardmanage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ItemCardManageInfoCrudqabBinding
import com.tegaoteam.application.tegao.ui.learning.cardmanage.model.QuickCrudItemInfo

class QuickCrudItemListAdapter: ListAdapter<QuickCrudItemInfo, QuickCrudItemListAdapter.ViewHolder>(DiffCallback()) {
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

    class ViewHolder private constructor(private val context: Context, private val binding: ItemCardManageInfoCrudqabBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: QuickCrudItemInfo) {
            binding.dataInfo = item

            val optionPopupMenu = PopupMenu(context, binding.optionsMenuBtn).apply {
                inflate(R.menu.quick_crud_menu)
                item.apply {
                    if (onExportQabClickListener == null) menu.findItem(R.id.crud_share).isVisible = false
                    if (onEditQabClickListener == null) menu.findItem(R.id.crud_edit).isVisible = false
                    if (onDeleteQabClickListener == null) menu.findItem(R.id.crud_delete).isVisible = false
                }
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.crud_share -> {
                            item.onExportQabClickListener?.invoke(item.id)
                            true
                        }
                        R.id.crud_edit -> {
                            item.onEditQabClickListener?.invoke(item.id)
                            true
                        }
                        R.id.crud_delete -> {
                            item.onDeleteQabClickListener?.invoke(item.id)
                            true
                        }
                        else -> false
                    }
                }
            }
            binding.optionsMenuBtn.setOnClickListener { optionPopupMenu.show() }

            binding.lifecycleOwner = item.lifecycleOwner
            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemCardManageInfoCrudqabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(parent.context, binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<QuickCrudItemInfo>() {
        override fun areItemsTheSame(
            oldItem: QuickCrudItemInfo,
            newItem: QuickCrudItemInfo
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: QuickCrudItemInfo,
            newItem: QuickCrudItemInfo
        ): Boolean {
            return oldItem == newItem
        }

    }
}