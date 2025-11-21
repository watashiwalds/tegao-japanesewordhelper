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
import com.tegaoteam.application.tegao.databinding.ItemChipCheckboxTextBinding
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipManager
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel
import com.tegaoteam.application.tegao.utils.preset.DialogPreset
import kotlin.getValue

class CardCreateSetTypeFragment: Fragment() {
    private lateinit var _binding: FragmentCardCreateValueSelectBinding
    private lateinit var _adapter: ThemedChipListAdapter<ItemChipCheckboxTextBinding>
    private val _parentViewModel: CardCreateActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_card_create_value_select, container, false)

        initVariables()
        initView()

        displayLastResult()

        return _binding.root
    }

    private fun initVariables() {
        _binding.lifecycleOwner = this
        _adapter = ThemedChipListAdapter(
            lifecycleOwner = viewLifecycleOwner,
            bindingInflater = ItemChipCheckboxTextBinding::inflate
        ).apply {
            themedChipManager = ThemedChipManager(ThemedChipManager.MODE_SINGLE)
        }
    }

    private fun initView() {
        _binding.loFragmentTitleText.setText(R.string.card_create_what_type)
        _binding.loSelectableGroupListRcy.adapter = _adapter
        _adapter.submitList(_parentViewModel.cardTypeChipItems.map { ThemedChipItem(
            id = it.first.toString(),
            label = it.second,
            _isSelected = MutableLiveData<Boolean>()
        ) })
        _binding.nextBtn.setOnClickListener {
            val selected = _adapter.themedChipManager?.selectedChips?.firstOrNull()?.id
            if (selected == null) {
                DialogPreset.requestConfirmation(
                    context = requireContext(),
                    title = 0,
                    message = R.string.card_create_error_no_type
                )
            } else {
                _parentViewModel.submitSelectedType(selected.toInt())
                findNavController().navigate(CardCreateSetTypeFragmentDirections.actionCardCreateSetTypeFragmentToCardCreateSetFrontFragment())
            }
        }
        _binding.executePendingBindings()
    }

    private fun displayLastResult() {
        _parentViewModel.selectedType?.let {
            _adapter.themedChipManager?.chips?.firstOrNull{ chip -> chip.id == it.toString() }?.nowSelected()
        }
    }
}