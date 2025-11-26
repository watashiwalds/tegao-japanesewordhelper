package com.tegaoteam.application.tegao.ui.learning.cardmanage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardManageDetailBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.ui.learning.cardcreate.model.CardPlaceholder
import com.tegaoteam.application.tegao.ui.learning.cardmanage.CardManageActivityViewModel
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.dpToPixel
import com.tegaoteam.application.tegao.utils.preset.DialogPreset
import timber.log.Timber
import kotlin.getValue

class CardManageEditCardFragment: Fragment() {
    private lateinit var _binding: FragmentCardManageDetailBinding
    private val _parentViewModel: CardManageActivityViewModel by activityViewModels()
    private var _cardId: Long? = null
    private lateinit var cardEntry: CardEntry
    private lateinit var cardGroups: List<CardGroup>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arriveArgs()

        _binding = FragmentCardManageDetailBinding.inflate(layoutInflater, container, false)

        initObservers()

        return _binding.root
    }

    private fun arriveArgs() {
        _cardId = CardManageEditCardFragmentArgs.fromBundle(requireArguments()).cardId
    }

    private fun initObservers() {
        observeCardLiveData(_parentViewModel.getCardById(_cardId?: 0))
        _parentViewModel.cardGroups.observe(viewLifecycleOwner) {
            cardGroups = it
            initView()
        }
    }

    private fun observeCardLiveData(liveData: LiveData<CardEntry>) {
        liveData.observe(viewLifecycleOwner) {
            if (it.cardId != _cardId) requireActivity().onBackPressedDispatcher.onBackPressed()
            cardEntry = it
            initView()
        }
    }

    private fun initView() {
        _binding.loFragmentTitleTxt.setText(R.string.card_manage_cardDetail_label)
        if (!::cardEntry.isInitialized || !::cardGroups.isInitialized) return

        val pad16 = dpToPixel(16f).toInt()
        val themedContext = ContextThemeWrapper(requireContext(), R.style.Theme_Tegao_ContentText_Normal)
        val fieldLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(pad16, 0, 0, pad16)
        }

        val cardPlaceholder = CardPlaceholder.fromDomainCardEntry(cardEntry)
        val groupField = AppCompatAutoCompleteTextView(themedContext).apply {
            layoutParams = fieldLayoutParams
            keyListener = null
            setHint(R.string.card_manage_cardDetail_field_group_hint)
            setOnClickListener { showDropDown() }
            onItemClickListener = AdapterView.OnItemClickListener { adapter, p1, position, p3 ->
                cardPlaceholder.groupId = cardGroups[position].groupId
            }
        }

        _binding.loDetailListLst.apply {
            removeAllViews()
            addView(AppCompatTextView(themedContext).apply {
                setPadding(0, 0, 0, pad16)
                text = getString(R.string.card_manage_cardDetail_field_dateCreated, cardEntry.dateCreated)
            })
            addView(AppCompatTextView(themedContext).apply { text = getString(R.string.card_manage_cardDetail_field_group_current, cardGroups.find { it.groupId == cardEntry.groupId }?.label) })
            addView(groupField.apply { setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, cardGroups.map { it.label })) })
        }

        _binding.deleteBtn.setOnClickListener {
            DialogPreset.requestConfirmation(
                context = requireContext(),
                title = getString(R.string.card_manage_delete_card_title, cardEntry.cardId.toString()),
                message = R.string.card_manage_delete_card_message,
                lambdaRun = { _parentViewModel.deleteCard(cardEntry.cardId) }
            )
        }
        _binding.updateBtn.setOnClickListener {
            DialogPreset.requestConfirmation(
                context = requireContext(),
                title = 0,
                message = R.string.card_manage_update_title,
                lambdaRun = {
                    _parentViewModel.updateCard(CardPlaceholder.toDomainCardEntry(cardPlaceholder))
                    AppToast.show(R.string.card_manage_update_confirm, AppToast.LENGTH_SHORT)
                }
            )
        }
    }
}