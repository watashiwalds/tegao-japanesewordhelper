package com.tegaoteam.application.tegao.ui.component.searchdisplay

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.databinding.ItemDefinitionWordBinding
import com.tegaoteam.application.tegao.databinding.ItemTagClassificationBinding
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.ui.component.tag.TagGroupListAdapter
import com.tegaoteam.application.tegao.ui.component.tag.TagItem
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityGate
import com.tegaoteam.application.tegao.ui.shared.DisplayHelper
import com.tegaoteam.application.tegao.utils.setTextWithVisibility
import com.tegaoteam.application.tegao.utils.toggleVisibility

class WordDefinitionCardListAdapter(private val lifecycleOwner: LifecycleOwner): ListAdapter<Word, WordDefinitionCardListAdapter.ViewHolder>(DiffCallback()) {
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
        holder.bind(getItem(position), lifecycleOwner)
    }

    class ViewHolder private constructor(private val context: Context, private val binding: ItemDefinitionWordBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word, lifecycleOwner: LifecycleOwner) {
            binding.reading.setTextWithVisibility(word.reading)
            if (word.reading.isNotBlank()) binding.reading.setTextIsSelectable(true)
            binding.furigana.setTextWithVisibility(word.furigana?.joinToString("、"))
            if (word.furigana?.isNotEmpty()?: false) binding.reading.setTextIsSelectable(true)

            binding.additionalInfo.setTextWithVisibility(word.additionalInfo?.joinToString("\n") { it.content })

            //display func for tags
            binding.loWordTagsRcy.layoutManager = DisplayHelper.FlexboxLayoutManagerMaker.rowStart(binding.loWordTagsRcy.context)
            binding.loWordTagsRcy.adapter = TagGroupListAdapter(ItemTagClassificationBinding::inflate).apply { submitList(
                word.tags?.map { TagItem.toTagItem(it.termKey, it.label, it.description) }
            ) }

            binding.loWordDefinitionsRcy.adapter = DefinitionListAdapter(lifecycleOwner).apply { submitList(word.definitions) }

            //functions binding for quick action buttons
            binding.qabMakeNewCardBtn.apply {
                val hasKey = word.reading.isNotBlank()
                toggleVisibility(hasKey)
                if (hasKey) setOnClickListener { context.startActivity(CardCreateActivityGate.departIntent(context, word)) }
            }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = ItemDefinitionWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(parent.context, binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Word>() {
        override fun areItemsTheSame(
            oldItem: Word,
            newItem: Word
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Word,
            newItem: Word
        ): Boolean {
            return oldItem == newItem
        }

    }
}