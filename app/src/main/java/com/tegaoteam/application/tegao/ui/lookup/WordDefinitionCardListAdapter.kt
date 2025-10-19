package com.tegaoteam.application.tegao.ui.lookup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.databinding.CardWordDefinitionBinding
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.ui.component.tag.TagGroupListAdapter
import com.tegaoteam.application.tegao.ui.component.tag.TagItem
import com.tegaoteam.application.tegao.ui.shared.DisplayFunctionMaker
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.TermBank

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

            //TODO: Write display func for tags / Write a TagGroup maker to inflate to the view
            val tags = word.tags?.map { (id, label) -> TagItem(
                label = label?: "",
                color = ContextCompat.getColor(TegaoApplication.instance.applicationContext, R.color.neutral),
                detail = TermBank.getByKey(id),
                clickListener = { tagItem -> AppToast.show(TegaoApplication.instance.applicationContext, tagItem.detail.toString(), AppToast.LENGTH_SHORT)}
            ) }
            binding.loWordTagsRcy.layoutManager = DisplayFunctionMaker.makeRowFlexboxLayoutManager(binding.loWordTagsRcy.context)
            binding.loWordTagsRcy.addItemDecoration(DisplayFunctionMaker.LinearDividerItemDecoration.make(
                0,
                TegaoApplication.instance.applicationContext.resources.getDimensionPixelSize(R.dimen.padding_nano)))
            binding.loWordTagsRcy.adapter = TagGroupListAdapter().apply { submitList(tags) }

            //TODO: Write DefinitionListAdapter to make definition list for RecyclerView

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