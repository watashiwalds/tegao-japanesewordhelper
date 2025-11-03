package com.tegaoteam.application.tegao.ui.setting

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.databinding.ActivitySettingBinding
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarBindingHelper
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

class SettingActivity : AppCompatActivity() {
    private lateinit var _binding: ActivitySettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_setting)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupHeaderBar()
    }

    private fun setupHeaderBar() {
        val navController = (supportFragmentManager.findFragmentById(R.id.mainActFragmentContainerView) as NavHostFragment).navController
        HeaderBarBindingHelper.bind(
            _binding.loHeaderBarIcl,
            getString(R.string.title_label_setting),
            backOnClickListener = { if (!navController.popBackStack()) finish() }
        )
    }
}