package com.tegaoteam.application.tegao.ui.learning.cardlearn

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
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.LearningHub
import com.tegaoteam.application.tegao.databinding.ActivityCardLearningBinding
import com.tegaoteam.application.tegao.ui.shared.BehaviorPreset

class CardLearningActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityCardLearningBinding
    private lateinit var _viewModel: CardLearningViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_card_learning)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        _viewModel = ViewModelProvider(this, CardLearningViewModel.Companion.ViewModelFactory(LearningHub()))[CardLearningViewModel::class.java]
        _viewModel.learnCardGroupId = (CardLearningActivityGate.arriveGroupId(intent))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus is EditText) BehaviorPreset.cancelInputWhenTouchOutside(
            motionEvent = ev,
            inputView = currentFocus!!,
            focusedView = currentFocus,
            imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager,
            currentFocus, findViewById(R.id.unv_customInputHolder_frm)
        )
        return super.dispatchTouchEvent(ev)
    }
}