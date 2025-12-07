package com.tegaoteam.application.tegao.ui.learning.cardcreate.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardCreateValueSelectBinding
import com.tegaoteam.application.tegao.databinding.ItemChipGroupLabelqabBinding
import com.tegaoteam.application.tegao.databinding.ItemChipToggableTextBinding
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipGroup
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipGroupListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipManager
import com.tegaoteam.application.tegao.ui.learning.LearningCardConst
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel
import com.tegaoteam.application.tegao.ui.learning.cardcreate.model.CardMaterial
import com.tegaoteam.application.tegao.ui.shared.DisplayHelper
import com.tegaoteam.application.tegao.ui.shared.preset.DialogPreset

class CardCreateSetFrontFragment: Fragment() {
    private lateinit var _binding: FragmentCardCreateValueSelectBinding
    private lateinit var _adapter: ThemedChipGroupListAdapter<ItemChipGroupLabelqabBinding>
    private val _parentViewModel: CardCreateActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_card_create_value_select, container, false)

        initVariables()
        initView()
        initObserver()

        return _binding.root
    }

    private fun initVariables() {
        _adapter = ThemedChipGroupListAdapter(viewLifecycleOwner, ItemChipGroupLabelqabBinding::inflate)
    }

    private fun initView() {
        _binding.loFragmentTitleText.setText(R.string.card_create_what_contentAtFront)
        _binding.loSelectableGroupListRcy.adapter = _adapter

        _binding.nextBtn.setOnClickListener {
            val selected = _adapter.currentList.map { it.getSelectedChips() }.flatten()
            if (selected.isEmpty()) {
                DialogPreset.requestConfirmation(
                    context = requireContext(),
                    title = 0,
                    message = R.string.card_create_error_no_content
                )
            } else {
                _parentViewModel.submitSelectedFront(selected.map {
                    val split = it.id.split("#")
                    split.first() to split.last().toInt()
                })
                when (_parentViewModel.selectedType) {
                    LearningCardConst.Type.TYPE_FLASHCARD.id -> {
                        findNavController().navigate(CardCreateSetFrontFragmentDirections.actionCardCreateSetFrontFragmentToCardCreateSetBackFragment())
                    }
                    LearningCardConst.Type.TYPE_ANSWERCARD.id -> {
                        findNavController().navigate(CardCreateSetFrontFragmentDirections.actionCardCreateSetFrontFragmentToCardCreateSetAnswerFragment())
                    }
                }
            }
        }
    }

    private fun initObserver() {
        _parentViewModel.cardMaterial.observe(viewLifecycleOwner) { contentMap ->
            val chipGroups = contentMap.contents.map { contentPack -> ThemedChipGroup(
                id = contentPack.key,
                label = CardMaterial.keyDisplayMap[contentPack.key]?: "-",
                manager = ThemedChipManager(ThemedChipManager.MODE_MULTI),
                listAdapter = ThemedChipListAdapter(viewLifecycleOwner, ItemChipToggableTextBinding::inflate),
                layoutManager = DisplayHelper.FlexboxLayoutManagerMaker.rowStart(requireContext()),
                allowQuickSelect = true
            ) }
            chipGroups.forEach {
                it.listAdapter.themedChipManager = it.manager
                it.listAdapter.submitList(contentMap.contents[it.id]?.mapIndexed { index, content -> ThemedChipItem(
                    id = "${it.id}#$index",
                    label = content,
                    _isSelected = MutableLiveData<Boolean>()
                ) } )
            }
            _adapter.submitList(chipGroups)
            displayLastResult()
        }
    }

    private fun displayLastResult() {
        _parentViewModel.selectedFronts?.let { selecteds ->
            val allChips = _adapter.currentList.map { group -> group.id to group.getChips() }
            selecteds.forEach { slt ->
                allChips.first { slt.first == it.first }.second[slt.second.toInt()].nowSelected()
            }
        }
    }
}