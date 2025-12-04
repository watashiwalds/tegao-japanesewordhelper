package com.tegaoteam.application.tegao.ui.setting.devmode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.FragmentDevPlaygroundBinding
import com.tegaoteam.application.tegao.domain.model.CardEntry
import com.tegaoteam.application.tegao.domain.model.CardRepeat
import com.tegaoteam.application.tegao.ui.learning.cardlearn.learnrun.SRSCalculation
import timber.log.Timber

class DevPlaygroundFragment : Fragment() {
    private lateinit var _binding: FragmentDevPlaygroundBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_dev_playground, container, false)
        viewModel = ViewModelProvider(this)[DevPlaygroundViewModel::class.java]

        initObservers()

        // Inflate the layout for this fragment
        return _binding.root
    }

    private lateinit var viewModel: DevPlaygroundViewModel
    private lateinit var testRepeat: CardRepeat
    private fun initObservers() {
        viewModel.groups.observe(viewLifecycleOwner) {
            viewModel.queryCardsByGroupId(it.first().groupId)
        }
        viewModel.queriedCardRepeat.observe(viewLifecycleOwner) {
            testRepeat = it.first()
            initTestRating()
        }
    }

    private lateinit var srsCal: SRSCalculation
    private fun initTestRating() {
        srsCal = SRSCalculation()
        srsCal.calculateRepeat(testRepeat)

        _binding.apply {
            repeatDataTxt.text = "${testRepeat.cardId}\n\t${testRepeat.lastRepeat}\n\t${testRepeat.nextRepeat}\n\t${testRepeat.easeFactor}"

            ratingEasyBtn.apply {
                srsCal.srsText_easy.observe(viewLifecycleOwner) { text = it }
                setOnClickListener {
                    val newRpt = srsCal.makeRepeatOfRating(SRSCalculation.RATING_EASY)
                    srsCal.calculateRepeat(newRpt)
                    repeatDataTxt.text = "${newRpt.cardId}\n\t${newRpt.lastRepeat}\n\t${newRpt.nextRepeat}\n\t${newRpt.easeFactor}"
                }
            }
            ratingGoodBtn.apply {
                srsCal.srsText_good.observe(viewLifecycleOwner) { text = it }
                setOnClickListener {
                    val newRpt = srsCal.makeRepeatOfRating(SRSCalculation.RATING_GOOD)
                    srsCal.calculateRepeat(newRpt)
                    repeatDataTxt.text = "${newRpt.cardId}\n\t${newRpt.lastRepeat}\n\t${newRpt.nextRepeat}\n\t${newRpt.easeFactor}"
                }
            }
            ratingHardBtn.apply {
                srsCal.srsText_hard.observe(viewLifecycleOwner) { text = it }
                setOnClickListener {
                    val newRpt = srsCal.makeRepeatOfRating(SRSCalculation.RATING_HARD)
                    srsCal.calculateRepeat(newRpt)
                    repeatDataTxt.text = "${newRpt.cardId}\n\t${newRpt.lastRepeat}\n\t${newRpt.nextRepeat}\n\t${newRpt.easeFactor}"
                }
            }
            ratingForgetBtn.apply {
                srsCal.srsText_forget.observe(viewLifecycleOwner) { text = it }
                setOnClickListener {
                    val newRpt = srsCal.makeRepeatOfRating(SRSCalculation.RATING_FORGET)
                    srsCal.calculateRepeat(newRpt)
                    repeatDataTxt.text = "${newRpt.cardId}\n\t${newRpt.lastRepeat}\n\t${newRpt.nextRepeat}\n\t${newRpt.easeFactor}"
                }
            }
        }
    }
}