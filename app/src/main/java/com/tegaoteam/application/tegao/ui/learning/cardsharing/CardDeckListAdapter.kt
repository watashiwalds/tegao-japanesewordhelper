package com.tegaoteam.application.tegao.ui.learning.cardsharing

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.data.database.cardpack.CardDeck
import com.tegaoteam.application.tegao.databinding.ItemCarddeckQuickinfoBinding

class CardDeckListAdapter(private val onDeckClickListener: (CardDeck) -> Unit): ListAdapter<CardDeck, CardDeckListAdapter.ViewHolder>(DiffCallback()) {
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
        holder.bind(getItem(position), onDeckClickListener)
    }

    class ViewHolder private constructor(private val binding: ItemCarddeckQuickinfoBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CardDeck, onDeckClickListener: (CardDeck) -> Unit) {
            binding.apply {
                label.text = item.label
                author.text = item.author
                description.text = item.description
                root.setOnClickListener { onDeckClickListener.invoke(item) }
                executePendingBindings()
            }
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemCarddeckQuickinfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<CardDeck>() {
        override fun areItemsTheSame(
            oldItem: CardDeck,
            newItem: CardDeck
        ): Boolean {
            return oldItem.link == newItem.link
        }

        override fun areContentsTheSame(
            oldItem: CardDeck,
            newItem: CardDeck
        ): Boolean {
            return oldItem == newItem
        }

    }
}