package com.tegaoteam.application.tegao.ui.options

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.tegaoteam.application.tegao.R
import com.tegaoteam.application.tegao.data.hub.OnlineServiceHub
import com.tegaoteam.application.tegao.databinding.ActivityOptionsBinding
import com.tegaoteam.application.tegao.ui.component.generics.HeaderBarBindingHelper
import com.tegaoteam.application.tegao.ui.component.generics.listnavigation.ListNavigationListAdapter
import com.tegaoteam.application.tegao.ui.account.SignInHelper
import com.tegaoteam.application.tegao.ui.setting.SettingActivity
import com.tegaoteam.application.tegao.utils.AppToast
import timber.log.Timber

class OptionsActivity : AppCompatActivity() {
    private lateinit var _binding: ActivityOptionsBinding
    private lateinit var _viewModel: OptionsActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_options)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        _viewModel = ViewModelProvider(this, OptionsActivityViewModel.Companion.ViewModelFactory(OnlineServiceHub()))[OptionsActivityViewModel::class.java]

        initView()

        setupAccountAction()

        _binding.lifecycleOwner = this
        _binding.executePendingBindings()
    }

    private fun initView() {
        HeaderBarBindingHelper.bind(
            headerBinding = _binding.loHeaderBarIcl,
            label = getString(R.string.title_label_options),
            backOnClickListener = { finish() }
        )
        _binding.optionsListRcy.apply {
            val _adapter = ListNavigationListAdapter().apply {
                navigatingFunction = { id ->
                    when (id) {
                        R.id.settingListFragment -> startActivity(Intent(this@OptionsActivity, SettingActivity::class.java))
                    }
                }
                submitList(_viewModel.navigationList)
            }
            adapter = _adapter
        }
    }

    private val _regSignInActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        Timber.d("Receive Google login result, send to SignInHelper")
        _signInHelper.processSignInResult(res) { userToken -> _viewModel.notifyLoginTokenToServer(userToken) }
    }
    private lateinit var _signInHelper: SignInHelper
    private fun setupAccountAction() {
        _signInHelper = SignInHelper(this, _regSignInActivityResult, _binding.loAccountCardIcl)
        _viewModel.evNotifiedToken.apply {
            beacon.observe(this@OptionsActivity) {
                if (receive()) AppToast.show(getMessage()?: "", AppToast.LENGTH_SHORT)
            }
        }
    }
}