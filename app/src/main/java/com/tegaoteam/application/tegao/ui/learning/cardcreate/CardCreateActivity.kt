package com.tegaoteam.application.tegao.ui.learning.cardcreate

import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.LearningHub
import com.tegaoteam.application.tegao.databinding.ActivityCardCreateBinding
import com.tegaoteam.application.tegao.domain.repo.LearningRepo
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarBindingHelper
import com.tegaoteam.application.tegao.ui.shared.BehaviorPreset

class CardCreateActivity : AppCompatActivity() {
    private lateinit var _viewModel: CardCreateActivityViewModel
    private lateinit var _binding: ActivityCardCreateBinding
    private lateinit var _learningRepo: LearningRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_card_create)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _learningRepo = LearningHub()
        _viewModel = ViewModelProvider(this, CardCreateActivityViewModel.Companion.ViewModelFactory(_learningRepo))[CardCreateActivityViewModel::class.java]

        setupHeaderBar()

        _viewModel.postCardContentMaterial(CardCreateActivityGate.arriveIntent(intent))
    }

    private fun setupHeaderBar() {
        val navController = (supportFragmentManager.findFragmentById(R.id.cardCreateFragmentContainerView) as NavHostFragment).navController
        HeaderBarBindingHelper.bind(
            _binding.loHeaderBarIcl,
            "Thêm thẻ học tập mới",
            { if (!navController.popBackStack()) finish() },
            { finish() }
            )
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus is EditText) BehaviorPreset.cancelInputWhenTouchOutside(
            ev,
            currentFocus!!,
            currentFocus,
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager,
            _binding.unvCustomInputHolderFrm, findViewById(R.id.view_inputBarView), currentFocus)
        return super.dispatchTouchEvent(ev)
    }
}