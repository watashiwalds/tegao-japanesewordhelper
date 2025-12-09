package com.tegaoteam.application.tegao.ui.component.searchdisplay

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.TegaoApplication
import com.tegaoteam.application.tegao.databinding.ItemChipCharacterPickBinding
import com.tegaoteam.application.tegao.databinding.ItemTagClassificationBinding
import com.tegaoteam.application.tegao.databinding.ViewTabDefinitionKanjisBinding
import com.tegaoteam.application.tegao.domain.model.Kanji
import com.tegaoteam.application.tegao.ui.component.tag.TagGroupListAdapter
import com.tegaoteam.application.tegao.ui.component.tag.TagItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipManager
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityGate
import com.tegaoteam.application.tegao.ui.shared.DisplayHelper
import com.tegaoteam.application.tegao.utils.setTextWithVisibility
import com.tegaoteam.application.tegao.utils.toggleVisibility
import timber.log.Timber

class KanjisDefinitionWidgetRecyclerAdapter(private val lifecycleOwner: LifecycleOwner, private var kanjiList: List<Kanji> = listOf(), private val onTabChangedListener: (String) -> Unit = {}): RecyclerView.Adapter<KanjisDefinitionWidgetRecyclerAdapter.WidgetManager>() {
    private val charPickAdapter = ThemedChipListAdapter(lifecycleOwner, ItemChipCharacterPickBinding::inflate)

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
        holder.initBind(kanjiList, onTabChangedListener)
    }

    override fun getItemCount(): Int {
        return 1
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<Kanji>) {
        kanjiList = list
        notifyDataSetChanged()
    }

    class WidgetManager private constructor(private val context: Context, private val lifecycleOwner: LifecycleOwner, private val binding: ViewTabDefinitionKanjisBinding, private val chipAdapter: ThemedChipListAdapter<ItemChipCharacterPickBinding>): RecyclerView.ViewHolder(binding.root) {
        fun initBind(list: List<Kanji>, onTabChangedListener: (String) -> Unit) {
            chipAdapter.themedChipManager = ThemedChipManager(ThemedChipManager.MODE_SINGLE)
            chipAdapter.submitList(list.map{ ThemedChipItem.fromKanji(it) })
            chipAdapter.themedChipManager?.apply {
                setChipsOnSelectedListener { kanjiChip ->
                    //TODO: Notify Adapter of character change when click
                    updateBind(list.find { it.character == kanjiChip.id }?: Kanji.default())
                    onTabChangedListener.invoke(kanjiChip.id)
                    Timber.i("Kanji selected -> [${kanjiChip.id}]")
                }
                selectFirst()
            }
            binding.ctrlKanjisTabPickRcy.adapter = chipAdapter
            updateBind(list.firstOrNull()?: Kanji.default())
            binding.executePendingBindings()
        }

        fun updateBind(kanji: Kanji) {
            var hasAdditionalInfo = (kanji.additionalInfo != null)
            for (item in kanji.additionalInfo?: listOf()) {
                if (item.content.isBlank()) {
                    hasAdditionalInfo = false
                    break
                }
            }
            binding.hasAdditionalInfo = hasAdditionalInfo
            val isExpanding = MutableLiveData<Boolean>().apply { value = false }
            binding.isExpanding = isExpanding

            if (hasAdditionalInfo) {
                val expandFunc = { isExpanding.value = !isExpanding.value!! }
                listOf(binding.collapseAdditionalInfoImg, binding.loAdditionalInfoAvailableTxt, binding.loExpandClickPaddingImg).forEach { it.setOnClickListener { expandFunc() } }
                binding.loAdditionalInfoRcy.adapter = AdditionalInfoListAdapter().apply { submitList(kanji.additionalInfo?.map { it.termKey to it.content }) }
            }

            binding.kanjiStroke.setTextWithVisibility(kanji.character)

            binding.loTagsRcy.apply {
                layoutManager = DisplayHelper.FlexboxLayoutManagerMaker.rowStart(context)
                if (itemDecorationCount == 0) addItemDecoration(DisplayHelper.LinearDividerItemDecoration.make(0, TegaoApplication.instance.applicationContext.resources.getDimensionPixelSize(R.dimen.padding_nano)))
                adapter = TagGroupListAdapter(ItemTagClassificationBinding::inflate).apply { submitRawTagList(kanji.tags?.map { it.termKey to it.label }) }
            }

            binding.meaning.setTextWithVisibility(kanji.meaning)

            binding.loKunyomiGrp.toggleVisibility( if (kanji.kunyomi != null) {
                binding.kunyomi.setTextWithVisibility(kanji.kunyomi?.joinToString("、"))
                binding.loTagKunyomiIcl.infoTag = TagItem.toTagItem("kunyomi")
                true
            } else {
                false
            })
            binding.loOnyomiGrp.toggleVisibility( if (kanji.onyomi != null) {
                binding.onyomi.setTextWithVisibility(kanji.onyomi?.joinToString("、"))
                binding.loTagOnyomiIcl.infoTag = TagItem.toTagItem("onyomi")
                true
            } else {
                false
            })
            binding.loCompositeGrp.toggleVisibility( if (kanji.composites?.joinToString { it.hanji?: "" }?.isNotBlank()?: false) {
                binding.composite.setTextWithVisibility(kanji.composites?.joinToString(", ") { "[${it.character}] ${it.hanji ?: ""}" })
                //TODO: Globalization by using @string value
                binding.loTagCompositeIcl.infoTag = TagItem.toTagItem("composite")
                true
            } else {
                false
            })

            //functions for qab button
            if (kanji.character.isNotBlank()) binding.qabMakeNewCardBtn.apply {
                toggleVisibility(true)
                setOnClickListener { context.startActivity(CardCreateActivityGate.departIntent(context, kanji)) }
            }

            binding.lifecycleOwner = lifecycleOwner
            binding.executePendingBindings()
        }
        companion object {
            fun from(lifecycleOwner: LifecycleOwner, parent: ViewGroup, adapter: ThemedChipListAdapter<ItemChipCharacterPickBinding>): WidgetManager {
                val binding = ViewTabDefinitionKanjisBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return WidgetManager(parent.context, lifecycleOwner, binding, adapter)
            }
        }
    }
}