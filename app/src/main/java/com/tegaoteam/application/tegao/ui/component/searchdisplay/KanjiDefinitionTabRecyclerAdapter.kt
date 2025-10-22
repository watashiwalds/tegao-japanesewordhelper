package com.tegaoteam.application.tegao.ui.component.searchdisplay

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.databinding.CardKanjiDefinitionBinding
import com.tegaoteam.application.tegao.databinding.ItemCharacterPickChipBinding
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.ui.component.tag.TagGroupListAdapter
import com.tegaoteam.application.tegao.ui.component.tag.TagItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.utils.toggleVisibility
import timber.log.Timber

class KanjiDefinitionTabRecyclerAdapter(private val lifecycleOwner: LifecycleOwner, private var kanjiList: List<Kanji> = listOf()): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        const val VIEWTYPE_TABLIST = 0
        const val VIEWTYPE_CARD = 1
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEWTYPE_TABLIST -> TabList.from(lifecycleOwner, parent)
            else -> CardDisplay.from(lifecycleOwner, parent)
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        when (holder) {
            is TabList -> holder.bind(kanjiList)
            is CardDisplay -> holder.bind(kanjiList.get(currentCharacterTab))
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return VIEWTYPE_TABLIST
        if (position > 0) return VIEWTYPE_CARD
        return super.getItemViewType(position)
    }

    override fun getItemCount(): Int {
        return when {
            kanjiList.isEmpty() -> 1
            else -> 2
        }
    }

    fun submitList(list: List<Kanji>) {
        kanjiList = list
    }

    private var currentCharacterTab: Int = 0

    class TabList private constructor(private val lifecycleOwner: LifecycleOwner, itemView: RecyclerView): RecyclerView.ViewHolder(itemView) {
        fun bind(kanjiList: List<Kanji>) {
            val adapter = ThemedChipListAdapter(lifecycleOwner, ItemCharacterPickChipBinding::inflate)
            adapter.submitListWithClickListener(kanjiList.map{ ThemedChipItem.fromKanji(it) }) { kanjiId ->
                //TODO: Notify Adapter of character change when click
                Timber.i("Kanji selected -> [$kanjiId]")
            }
            (itemView as RecyclerView).adapter = adapter
        }
        companion object {
            fun from(lifecycleOwner: LifecycleOwner, parent: ViewGroup): TabList {
                return TabList(lifecycleOwner, RecyclerView(parent.context))
            }
        }
    }

    class CardDisplay private constructor(private val lifecycleOwner: LifecycleOwner, private val binding: CardKanjiDefinitionBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(kanji: Kanji) {
            binding.lifecycleOwner = lifecycleOwner

            val hasAdditionalInfo = (kanji.additionalInfo != null)
            binding.hasAdditionalInfo = hasAdditionalInfo
            val isExpanding = MutableLiveData<Boolean>().apply { value = false }
            binding.isExpanding = isExpanding

            if (hasAdditionalInfo) {
                val expandFunc = { isExpanding.value = !isExpanding.value!! }
                listOf(binding.collapseAdditionalInfoImg, binding.loAdditionalInfoAvailableTxt, binding.loExpandClickPaddingImg).forEach { it.setOnClickListener { expandFunc } }
                binding.loAdditionalInfoRcy.adapter = AdditionalInfoListAdapter().apply { submitList(kanji.additionalInfo) }
            }

            binding.kanjiStroke.text = kanji.character
            binding.loTagsRcy.adapter = TagGroupListAdapter().apply { submitRawTagList(kanji.tags) }
            binding.meaning.text = kanji.meaning

            binding.loKunyomiGrp.toggleVisibility( if (kanji.kunyomi != null) {
                binding.kunyomi.text = kanji.kunyomi
                binding.loTagKunyomiIcl.infoTag = TagItem.toTagItem("kunyomi", "Ku")
                true
            } else {
                false
            })
            binding.loOnyomiGrp.toggleVisibility( if (kanji.onyomi != null) {
                binding.onyomi.text = kanji.onyomi
                binding.loTagOnyomiIcl.infoTag = TagItem.toTagItem("onyomi", "On")
                true
            } else {
                false
            })
            binding.loCompositeGrp.toggleVisibility( if (kanji.composites != null) {
                binding.composite.text = kanji.composites?.joinToString(", ") { "[${it.first}] ${it.second ?: ""}" }
                //TODO: Globalization by using @string value
                binding.loTagCompositeIcl.infoTag = TagItem.toTagItem("composite", "Bộ")
                true
            } else {
                false
            })

            binding.executePendingBindings()
        }
        companion object {
            fun from(lifecycleOwner: LifecycleOwner, parent: ViewGroup): CardDisplay {
                val binding = CardKanjiDefinitionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return CardDisplay(lifecycleOwner, binding)
            }
        }
    }

    sealed class DisplayItem {
        object TabList: DisplayItem()
        object CardDisplay: DisplayItem()
    }
}