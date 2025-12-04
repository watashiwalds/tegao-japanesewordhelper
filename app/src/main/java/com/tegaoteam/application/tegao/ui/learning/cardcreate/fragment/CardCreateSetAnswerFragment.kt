package com.tegaoteam.application.tegao.ui.learning.cardcreate.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.AddonHub
import com.tegaoteam.application.tegao.databinding.FragmentCardCreateValueInputBinding
import com.tegaoteam.application.tegao.databinding.ItemTagGeneralTextBlockBinding
import com.tegaoteam.application.tegao.ui.component.generics.InputBarView
import com.tegaoteam.application.tegao.ui.component.handwriting.WritingViewBindingHelper
import com.tegaoteam.application.tegao.ui.component.tag.TagGroupListAdapter
import com.tegaoteam.application.tegao.ui.component.tag.TagItem
import com.tegaoteam.application.tegao.ui.learning.cardcreate.CardCreateActivityViewModel
import com.tegaoteam.application.tegao.ui.shared.DisplayHelper
import com.tegaoteam.application.tegao.utils.dpToPixel
import com.tegaoteam.application.tegao.utils.preset.DialogPreset
import timber.log.Timber
import kotlin.getValue

class CardCreateSetAnswerFragment: Fragment() {
    private lateinit var _binding: FragmentCardCreateValueInputBinding
    private lateinit var _inputBarView: InputBarView
    private lateinit var _quickInputAdapter: TagGroupListAdapter<*>
    private val _parentViewModel: CardCreateActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_card_create_value_input,
            container,
            false
        )

        initVariables()
        initView()
        initObservers()
        initAddons()

        return _binding.root
    }

    private fun initVariables() {
        _inputBarView = InputBarView(requireContext(), viewLifecycleOwner, AddonHub())
    }

    private fun initView() {
        _binding.loFragmentTitleText.setText(R.string.card_create_what_answer)

        _quickInputAdapter = TagGroupListAdapter(ItemTagGeneralTextBlockBinding::inflate)
        _binding.loInputFieldListLst.apply {
            removeAllViews()
            addView(_inputBarView.view)
            addView(RecyclerView(requireContext()).apply {
                layoutManager = DisplayHelper.FlexboxLayoutManagerMaker.rowStart(requireContext())
                adapter = _quickInputAdapter
                setPadding(0, dpToPixel(16f).toInt(), 0, 0)
            })
        }
        _binding.executePendingBindings()

        _binding.nextBtn.setOnClickListener {
            val selectedAnswer = _inputBarView.getInputValue().lowercase()
            isAnswerMeetConstraints(selectedAnswer)
        }
    }

    private fun submitAnswer(answer: String?) {
        _parentViewModel.submitSelectedAnswer(answer)
        findNavController().navigate(CardCreateSetAnswerFragmentDirections.actionCardCreateSetAnswerFragmentToCardCreateSetBackFragment())
    }

    private fun initObservers() {
        _parentViewModel.cardMaterial.observe(viewLifecycleOwner) { materials ->
            val usedMats = _parentViewModel.selectedFronts
            val allQuickInputTags = materials.contents.map { pack ->
                pack.value.mapIndexed { index, mat ->
                    TagItem(
                        label = mat,
                        backgroundResId = if (usedMats?.contains(Pair(pack.key, index)) == true) R.drawable.neutral_solid_background else R.drawable.neutral_stroke_underline,
                        clickListener = { tag ->
                            _inputBarView.getEditTextView().editableText.append(mat)
                        }
                    )
                }
            }.flatten()
            _quickInputAdapter.submitList(allQuickInputTags)
        }
    }

    private fun initAddons() {
        // it has handwriting support, so it need to bind the addon
        WritingViewBindingHelper.fullSuggestionBoard(
            AddonHub(),
            activity = requireActivity() as AppCompatActivity,
            linkedEditText = _inputBarView.getEditTextView(),
            boardHolder = requireActivity().findViewById(R.id.unv_customInputHolder_frm),
            switchButtonBinding = _inputBarView.getSwitchButton()
        )
    }

    private fun isAnswerMeetConstraints(ans: String?) {
        if (ans == null || ans.isBlank()) {
            DialogPreset.requestConfirmation(
                context = requireContext(),
                title = R.string.card_create_warning_empty_answer_label,
                message = R.string.card_create_warning_empty_answer_message,
                lambdaRun = { submitAnswer(null) }
            )
        } else if (ans.length > 30) {
            DialogPreset.requestConfirmation(
                context = requireContext(),
                title = R.string.card_create_warning_lengthy_answer_label,
                message = R.string.card_create_warning_lengthy_answer_message,
                lambdaRun = { submitAnswer(ans) }
            )
        } else {
            submitAnswer(ans)
        }
    }
}