package com.tegaoteam.application.tegao.ui.lookup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.databinding.CardWordDefinitionBinding
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.ui.component.tag.TagGroupListAdapter
import com.tegaoteam.application.tegao.ui.shared.DisplayFunctionMaker

class WordDefinitionCardListAdapter: ListAdapter<Word, WordDefinitionCardListAdapter.ViewHolder>(DiffCallback()) {
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

    class ViewHolder private constructor(private val binding: CardWordDefinitionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(word: Word) {
            binding.reading.text = word.reading
            binding.furigana.text = word.furigana

            //TODO: Figured out what was this intended to be for Jitendex and Jisho. For Mazii, it was just pronunciations
            binding.additionalInfo.text = word.additionalInfo?.joinToString("\n") { it.second }

            //display func for tags
            binding.loWordTagsRcy.layoutManager = DisplayFunctionMaker.makeRowFlexboxLayoutManager(binding.loWordTagsRcy.context)
            binding.loWordTagsRcy.addItemDecoration(DisplayFunctionMaker.LinearDividerItemDecoration.make(0, TegaoApplication.instance.applicationContext.resources.getDimensionPixelSize(R.dimen.padding_nano)))
            binding.loWordTagsRcy.adapter = TagGroupListAdapter().apply { submitRawTagList(word.tags) }

            //TODO: Write DefinitionListAdapter to make definition list for RecyclerView
            binding.loWordDefinitionsRcy.adapter = DefinitionListAdapter().apply { submitList(word.definitions) }

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = CardWordDefinitionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
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