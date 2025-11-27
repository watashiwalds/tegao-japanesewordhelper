package com.tegaoteam.application.tegao.ui.learning.cardmanage.fragment

import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentCardManageDetailBinding
import com.tegaoteam.application.tegao.databinding.ItemChipToggableTextBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardGroup
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipItem
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipListAdapter
import com.tegaoteam.application.tegao.ui.component.themedchip.ThemedChipManager
import com.tegaoteam.application.tegao.ui.learning.LearningCardConst
import com.tegaoteam.application.tegao.ui.learning.cardcreate.model.CardPlaceholder
import com.tegaoteam.application.tegao.ui.learning.cardmanage.CardManageActivityViewModel
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.dpToPixel
import com.tegaoteam.application.tegao.utils.preset.DialogPreset
import com.tegaoteam.application.tegao.utils.toggleVisibility
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

        initVariables()
        initObservers()

        return _binding.root
    }

    private fun arriveArgs() {
        _cardId = CardManageEditCardFragmentArgs.fromBundle(requireArguments()).cardId
    }

    private fun initVariables() {
        preInitViewComponents()
    }

    private fun initObservers() {
        observeCardLiveData(_parentViewModel.getCardById(_cardId?: 0))
        _parentViewModel.cardGroups.observe(viewLifecycleOwner) {
            cardGroups = it
            _groupFieldAdapter.apply {
                clear();
                addAll(it.map { it -> it.label });
                notifyDataSetChanged()
            }
        }
    }

    private fun observeCardLiveData(liveData: LiveData<CardEntry>) {
        liveData.observe(viewLifecycleOwner) {
            if (it.cardId != _cardId) requireActivity().onBackPressedDispatcher.onBackPressed()
            else {
                cardEntry = it
                initView()
            }
        }
    }

    private lateinit var _groupFieldAdapter: ArrayAdapter<String>
    private lateinit var _groupField: AppCompatAutoCompleteTextView
    private lateinit var _typeField: RecyclerView
    private val _showAnswerField = MutableLiveData<Boolean>().apply { value = false }
    private fun preInitViewComponents() {
        val pad16 = dpToPixel(16f).toInt()
        val themedContext = ContextThemeWrapper(requireContext(), R.style.Theme_Tegao_ContentText_Normal)
        val fieldLayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(pad16, 0, 0, pad16)
        }

        _groupFieldAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, mutableListOf<String>())
        _groupField = AppCompatAutoCompleteTextView(themedContext).apply {
            layoutParams = fieldLayoutParams
            keyListener = null
            setHint(R.string.card_manage_cardDetail_field_group_hint)
            onFocusChangeListener = View.OnFocusChangeListener { p0, focused -> if (focused) showDropDown() }
            onItemClickListener = AdapterView.OnItemClickListener { adapter, p1, position, p3 ->
                cardPlaceholder.groupId = cardGroups[position].groupId
            }
        }
        _typeField = RecyclerView(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(fieldLayoutParams).apply { setMargins(pad16, pad16/2, 0, pad16) }
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = ThemedChipListAdapter(viewLifecycleOwner, ItemChipToggableTextBinding::inflate).apply {
                themedChipManager = ThemedChipManager(ThemedChipManager.MODE_SINGLE)
                submitList(listOf(
                    ThemedChipItem(
                        id = LearningCardConst.Type.TYPE_FLASHCARD.id.toString(),
                        label = getString(R.string.card_type_flashcard_name),
                        _isSelected = MutableLiveData<Boolean>()
                    ),
                    ThemedChipItem(
                        id = LearningCardConst.Type.TYPE_ANSWERCARD.id.toString(),
                        label = getString(R.string.card_type_answercard_name),
                        _isSelected = MutableLiveData<Boolean>()
                    )
                ))
            }
        }
    }

    private var cardPlaceholder: CardPlaceholder = CardPlaceholder.fromDomainCardEntry(CardEntry.default())
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

        _showAnswerField.removeObservers(viewLifecycleOwner)

        cardPlaceholder = CardPlaceholder.fromDomainCardEntry(cardEntry)

        _binding.loDetailListLst.apply {
            removeAllViews()
            addView(AppCompatTextView(themedContext).apply {
                setPadding(0, 0, 0, pad16)
                text = getString(R.string.card_manage_cardDetail_field_dateCreated, cardEntry.dateCreated)
            })
            addView(AppCompatTextView(themedContext).apply {
                text = getString(R.string.card_manage_cardDetail_field_group_current, cardGroups.find { it.groupId == cardEntry.groupId }?.label)
            })
            addView(_groupField.apply {
                setAdapter(_groupFieldAdapter)
            })
            addView(AppCompatTextView(themedContext).apply {
                text = getString(R.string.card_manage_cardDetail_field_type, when (cardEntry.type) {
                    LearningCardConst.Type.TYPE_FLASHCARD.id -> getString(R.string.card_type_flashcard_name)
                    LearningCardConst.Type.TYPE_ANSWERCARD.id -> getString(R.string.card_type_answercard_name)
                    else -> ""
                })
            })
            addView(_typeField.apply {
                (adapter as ThemedChipListAdapter<*>).themedChipManager!!.apply {
                    this.setChipsOnSelectedListener { item ->
                        cardPlaceholder.type = item.id.toInt()
                        _showAnswerField.value = (item.id == LearningCardConst.Type.TYPE_ANSWERCARD.id.toString())
                        Timber.i("${_showAnswerField.value}")
                    }
                    chips.firstOrNull {it -> it.id == cardEntry.type.toString()}?.nowSelected()
                }
            })
            addView(AppCompatTextView(themedContext).apply {
                setText(R.string.card_manage_cardDetail_field_answer)
                _showAnswerField.observe(viewLifecycleOwner) { toggleVisibility(it) }
            })
            addView(AppCompatEditText(themedContext).apply {
                layoutParams = fieldLayoutParams
                isSingleLine = true
                inputType = InputType.TYPE_CLASS_TEXT
                editableText.append(cardEntry.answer?: "")
                onFocusChangeListener = View.OnFocusChangeListener { p0, focused -> if (!focused) cardPlaceholder.answer = text.toString() }
                _showAnswerField.observe(viewLifecycleOwner) { toggleVisibility(it) }
            })
            addView(AppCompatTextView(themedContext).apply {
                setText(R.string.card_manage_cardDetail_field_front)
            })
            addView(AppCompatEditText(themedContext).apply {
                layoutParams = fieldLayoutParams
                isSingleLine = false
                gravity = Gravity.START or Gravity.TOP
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                editableText.append(cardEntry.front)
                onFocusChangeListener = View.OnFocusChangeListener { p0, focused -> if (!focused) cardPlaceholder.front = text.toString() }
            })
            addView(AppCompatTextView(themedContext).apply {
                setText(R.string.card_manage_cardDetail_field_back)
            })
            addView(AppCompatEditText(themedContext).apply {
                layoutParams = fieldLayoutParams
                isSingleLine = false
                gravity = Gravity.START or Gravity.TOP
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                editableText.append(cardEntry.back)
                onFocusChangeListener = View.OnFocusChangeListener { p0, focused -> if (!focused) cardPlaceholder.back = text.toString() }
            })
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

        _binding.lifecycleOwner = viewLifecycleOwner
        _binding.executePendingBindings()
    }
}