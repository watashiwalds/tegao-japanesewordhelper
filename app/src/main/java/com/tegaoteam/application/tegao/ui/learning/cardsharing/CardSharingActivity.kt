package com.tegaoteam.application.tegao.ui.learning.cardsharing

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ActivityCardSharingBinding
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarBindingHelper

class CardSharingActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityCardSharingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_card_sharing)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initView()
    }

    private fun initView() {
        HeaderBarBindingHelper.bind(
            headerBinding = _binding.loHeaderBarIcl,
            label = getString(R.string.card_sharing_headerLabel),
            backOnClickListener = { finish() }
        )
    }
}