package com.tegaoteam.application.tegao.ui.component.searchdisplay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.databinding.SubitemDefinitionSingleWordBinding
import com.tegaoteam.application.tegao.domain.model.Word
import com.tegaoteam.application.tegao.ui.component.tag.TagGroupListAdapter
import com.tegaoteam.application.tegao.ui.shared.DisplayHelper

class DefinitionListAdapter(private val lifecycleOwner: LifecycleOwner): ListAdapter<Word.Definition, DefinitionListAdapter.ViewHolder>(DiffCallback()) {
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
        holder.bind(position, getItem(position), lifecycleOwner)
    }

    class ViewHolder private constructor(private val binding: SubitemDefinitionSingleWordBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(index: Int, definition: Word.Definition, lifecycleOwner: LifecycleOwner) {
            binding.loIndexTxt.text = String.format("${index+1}")

            binding.loDefinitionTagsRcy.layoutManager = DisplayHelper.FlexboxLayoutManagerMaker.rowStart(binding.loDefinitionTagsRcy.context)
            if (binding.loDefinitionTagsRcy.itemDecorationCount == 0) binding.loDefinitionTagsRcy.addItemDecoration(DisplayHelper.LinearDividerItemDecoration.make(0, TegaoApplication.instance.applicationContext.resources.getDimensionPixelSize(R.dimen.padding_nano)))
            binding.loDefinitionTagsRcy.adapter = TagGroupListAdapter().apply { submitRawTagList(definition.tags) }

            binding.definition.text = definition.meaning

            val expandable = definition.expandInfos != null && definition.expandInfos!!.isNotEmpty()
            binding.expandable = expandable
            val isExpanding = MutableLiveData<Boolean>().apply { value = false }
            binding.isExpanding = isExpanding

            if (expandable) {
                val expandFun = { isExpanding.value = !isExpanding.value!! }
                listOf(
                    binding.loIndexTxt,
                    binding.expandDefinitionImg,
                    binding.collapseDefinitionImg,
                    binding.loExpandClickPaddingImg,
                    binding.definition,
                    binding.loDefinitionTagsRcy
                ).forEach { it.setOnClickListener { expandFun() } }
                binding.lifecycleOwner = lifecycleOwner
            }

            binding.loDefinitionExpandInfosRcy.adapter = AdditionalInfoListAdapter().apply { submitList(definition.expandInfos) }

            binding.executePendingBindings()
        }
        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding = SubitemDefinitionSingleWordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class DiffCallback: DiffUtil.ItemCallback<Word.Definition>() {
        override fun areItemsTheSame(
            oldItem: Word.Definition,
            newItem: Word.Definition
        ): Boolean {
            return oldItem.meaning == newItem.meaning
        }

        override fun areContentsTheSame(
            oldItem: Word.Definition,
            newItem: Word.Definition
        ): Boolean {
            return oldItem == newItem
        }

    }
}