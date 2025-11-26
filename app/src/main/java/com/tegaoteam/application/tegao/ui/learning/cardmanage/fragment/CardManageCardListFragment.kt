package com.tegaoteam.application.tegao.ui.learning.cardmanage.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardManageQuickcrudListBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.ui.homescreen.learning.LearningInfoDataClasses
import com.tegaoteam.application.tegao.ui.learning.cardmanage.CardManageActivityViewModel
import com.tegaoteam.application.tegao.ui.learning.cardmanage.adapter.QuickCrudItemListAdapter
import com.tegaoteam.application.tegao.utils.preset.DialogPreset
import com.tegaoteam.application.tegao.utils.toggleVisibility
import kotlin.getValue

class CardManageCardListFragment: Fragment() {
    private lateinit var _binding: FragmentCardManageQuickcrudListBinding
    private val _parentViewModel: CardManageActivityViewModel by activityViewModels()
    private lateinit var _adapter: QuickCrudItemListAdapter
    private lateinit var _navController: NavController
    private var _groupId: Long? = null
    private lateinit var cardGroup: CardGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCardManageQuickcrudListBinding.inflate(inflater, container, false)

        initVariables()
        initObservers()
        initView()

        return _binding.root
    }

    private fun initVariables() {
        _groupId = CardManageCardListFragmentArgs.fromBundle(requireArguments()).groupId
        _adapter = QuickCrudItemListAdapter()
        _navController = findNavController()
    }

    private fun initObservers() {
        _parentViewModel.apply {
            cardGroups.observe(viewLifecycleOwner) {
                val res = it.firstOrNull { grp -> grp.groupId == _groupId }
                if (res == null) requireActivity().onBackPressedDispatcher.onBackPressed()
                else {
                    cardGroup = res
                    _binding.loFragmentTitleTxt.text = getString(R.string.card_manage_cardList_label, res.label)
                    observeCardListLiveData(_parentViewModel.fetchCardsOfGroupLiveData(res.groupId))
                }
            }
        }
    }

    private fun observeCardListLiveData(liveData: LiveData<List<CardEntry>>) {
        val toEditLambda = { cardId: Long -> _navController.navigate(CardManageCardListFragmentDirections.actionCardManageCardListFragmentToCardManageEditCardFragment(cardId)) }
        liveData.observe(viewLifecycleOwner) { cards ->
            _adapter.submitList( cards.map { card ->
                LearningInfoDataClasses.QuickCrudItemInfo(
                    id = card.cardId,
                    label = getString(R.string.card_manage_cardItem_label, "${card.cardId}"),
                    quickInfo = MutableLiveData<String>().apply { value = card.front.split("\n").apply { subList(0, if (this.size < 2) this.size else 2) }.joinToString("\n") },
                    onEditQabClickListener = toEditLambda,
                    onDeleteQabClickListener = { cardId ->
                        DialogPreset.requestConfirmation(
                            context = requireContext(),
                            title = getString(R.string.card_manage_delete_card_title, card.cardId.toString()),
                            message = R.string.card_manage_delete_card_message,
                            lambdaRun = { _parentViewModel.deleteCard(cardId) }
                        )
                    },
                    onItemClickListener = toEditLambda
                )
            } )
            _binding.itemCountTxt.text = getString(R.string.card_manage_card_count, cards.size)
        }
    }

    private fun initView() {
        _binding.placeholderQabBtn.toggleVisibility(false)
        _binding.quickCrudListRcy.adapter = _adapter
        _binding.executePendingBindings()
    }
}