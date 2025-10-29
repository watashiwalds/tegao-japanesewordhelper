package com.tegaoteam.application.tegao.ui.option

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ActivityOptionBinding
import com.tegaoteam.application.tegao.ui.component.headerbar.HeaderBarBindingHelper

class OptionActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityOptionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_option)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupHeaderBar()
    }

    private fun setupHeaderBar() {
        HeaderBarBindingHelper.bind(
            _binding.loHeaderBarIcl,
            getString(R.string.title_label_option),
            backOnClickListener = { finish() } //TODO: Make NavHostFragment to nav options and replace this listener with proper NavUI managing code
        )
    }
}