package com.tegaoteam.application.tegao.ui.learning.cardcreate

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.LearningHub
import com.tegaoteam.application.tegao.databinding.ActivityCardCreateBinding
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarBindingHelper

class CardCreateActivity : AppCompatActivity() {
    private lateinit var _viewModel: CardCreateActivityViewModel
    private lateinit var _binding: ActivityCardCreateBinding
    private lateinit var _learningRepo: LearningRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_card_create)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _learningRepo = LearningHub()
        _viewModel = ViewModelProvider(this, CardCreateActivityViewModel.Companion.ViewModelFactory(_learningRepo))[CardCreateActivityViewModel::class.java]
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_card_create)

        setupHeaderBar()

        _viewModel.postCardContentMaterial(CardCreateActivityGate.arriveIntent(intent))
    }

    private fun setupHeaderBar() {
        HeaderBarBindingHelper.bind(
            _binding.loHeaderBarIcl,
            "Thêm thẻ học tập mới",
            null,
            { finish() }
            )
    }
}