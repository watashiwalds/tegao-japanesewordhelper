package com.tegaoteam.application.tegao.ui.lookup

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ActivityLookupBinding

class LookupActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityLookupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_lookup)
        _binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(_binding.beforeMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        makeStartState()
    }

    private fun makeStartState() {
        _binding.keywordInputEdt.requestFocus()
    }
}