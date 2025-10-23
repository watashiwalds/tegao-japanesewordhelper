package com.tegaoteam.application.tegao.ui.component.searchdisplay

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.databinding.ItemCharacterPickChipBinding
import com.tegaoteam.application.tegao.databinding.WidgetKanjisDefinitionTabBinding
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.ui.component.tag.TagGroupListAdapter
import com.tegaoteam.application.tegao.ui.component.tag.TagItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.shared.DisplayFunctionMaker
import com.tegaoteam.application.tegao.utils.setTextWithVisibility
import com.tegaoteam.application.tegao.utils.toggleVisibility
import timber.log.Timber

class KanjisDefinitionWidgetRecyclerAdapter(private val lifecycleOwner: LifecycleOwner, private var kanjiList: List<Kanji> = listOf()): RecyclerView.Adapter<KanjisDefinitionWidgetRecyclerAdapter.WidgetManager>() {
    private val charPickAdapter = ThemedChipListAdapter(lifecycleOwner, ItemCharacterPickChipBinding::inflate)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WidgetManager {
        return WidgetManager.from(lifecycleOwner, parent, charPickAdapter)
    }

    override fun onBindViewHolder(
        holder: WidgetManager,
        position: Int
    ) {
        holder.initBind(kanjiList)
    }

    override fun getItemCount(): Int {
        return 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Kanji>) {
        kanjiList = list
        notifyDataSetChanged()
    }

    class WidgetManager private constructor(private val lifecycleOwner: LifecycleOwner, private val binding: WidgetKanjisDefinitionTabBinding, private val chipAdapter: ThemedChipListAdapter<ItemCharacterPickChipBinding>): RecyclerView.ViewHolder(binding.root) {
        fun initBind(list: List<Kanji>) {
            chipAdapter.submitListWithClickListener(list.map{ ThemedChipItem.fromKanji(it) }) { kanjiId ->
                //TODO: Notify Adapter of character change when click
                updateBind(list.find { it.character == kanjiId }?: Kanji.default())
                Timber.i("Kanji selected -> [$kanjiId]")
            }
            binding.ctrlKanjisTabPickRcy.adapter = chipAdapter
            updateBind(list.firstOrNull()?: Kanji.default())
            binding.executePendingBindings()
        }

        fun updateBind(kanji: Kanji) {
            val hasAdditionalInfo = (kanji.additionalInfo != null && !kanji.additionalInfo!!.isEmpty())
            binding.hasAdditionalInfo = hasAdditionalInfo
            val isExpanding = MutableLiveData<Boolean>().apply { value = false }
            binding.isExpanding = isExpanding

            if (hasAdditionalInfo) {
                val expandFunc = { isExpanding.value = !isExpanding.value!! }
                listOf(binding.collapseAdditionalInfoImg, binding.loAdditionalInfoAvailableTxt, binding.loExpandClickPaddingImg).forEach { it.setOnClickListener { expandFunc() } }
                binding.loAdditionalInfoRcy.adapter = AdditionalInfoListAdapter().apply { submitList(kanji.additionalInfo) }
            }

            binding.kanjiStroke.setTextWithVisibility(kanji.character)

            binding.loTagsRcy.apply {
                layoutManager = DisplayFunctionMaker.makeRowFlexboxLayoutManager(context)
                if (itemDecorationCount == 0) addItemDecoration(DisplayFunctionMaker.LinearDividerItemDecoration.make(0, TegaoApplication.instance.applicationContext.resources.getDimensionPixelSize(R.dimen.padding_nano)))
                adapter = TagGroupListAdapter().apply { submitRawTagList(kanji.tags) }
            }

            binding.meaning.setTextWithVisibility(kanji.meaning)

            binding.loKunyomiGrp.toggleVisibility( if (kanji.kunyomi != null) {
                binding.kunyomi.setTextWithVisibility(kanji.kunyomi)
                binding.loTagKunyomiIcl.infoTag = TagItem.toTagItem("kunyomi", "Ku")
                true
            } else {
                false
            })
            binding.loOnyomiGrp.toggleVisibility( if (kanji.onyomi != null) {
                binding.onyomi.setTextWithVisibility(kanji.onyomi)
                binding.loTagOnyomiIcl.infoTag = TagItem.toTagItem("onyomi", "On")
                true
            } else {
                false
            })
            binding.loCompositeGrp.toggleVisibility( if (kanji.composites != null) {
                binding.composite.setTextWithVisibility(kanji.composites?.joinToString(", ") { "[${it.first}] ${it.second ?: ""}" })
                //TODO: Globalization by using @string value
                binding.loTagCompositeIcl.infoTag = TagItem.toTagItem("composite", "Bộ")
                true
            } else {
                false
            })

            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
        }
        companion object {
            fun from(lifecycleOwner: LifecycleOwner, parent: ViewGroup, adapter: ThemedChipListAdapter<ItemCharacterPickChipBinding>): WidgetManager {
                val binding = WidgetKanjisDefinitionTabBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return WidgetManager(lifecycleOwner, binding, adapter)
            }
        }
    }
}