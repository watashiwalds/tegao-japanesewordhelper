package com.tegaoteam.application.tegao.ui.homescreen.lookup.searchhistory

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.BR
import timber.log.Timber

class SearchHistoryListAdapter<T: ViewDataBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup, Boolean) -> T,
    private val clickListener: (keywork: String) -> Unit
): ListAdapter<SearchHistoryItem, SearchHistoryListAdapter<T>.ViewHolder>( DiffCallback() ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = bindingInflater(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<SearchHistoryItem?>?) {
        super.submitList(list)
        Timber.i("Receive LiveData<History> size ${list?.size}")
    }

    inner class ViewHolder(val binding: T): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchHistoryItem) {
            binding.setVariable(BR.searchedKeyword, item.keyword)
            binding.root.setOnClickListener { clickListener(item.keyword) }
            binding.executePendingBindings()
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<SearchHistoryItem>() {
        override fun areItemsTheSame(
            oldItem: SearchHistoryItem,
            newItem: SearchHistoryItem
        ): Boolean {
            return oldItem.keyword == newItem.keyword
        }

        override fun areContentsTheSame(
            oldItem: SearchHistoryItem,
            newItem: SearchHistoryItem
        ): Boolean {
            return oldItem == newItem
        }

    }
}