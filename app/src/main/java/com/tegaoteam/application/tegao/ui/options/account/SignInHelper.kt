package com.tegaoteam.application.tegao.ui.options.account

import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.tegaoteam.application.tegao.databinding.IncludeAccountInfoIoBinding
import com.tegaoteam.application.tegao.utils.setTextWithVisibility
import com.tegaoteam.application.tegao.utils.toggleVisibility

class SignInHelper(private val activity: AppCompatActivity) {

    private var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val REQ_CODE = 1000

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("135658289710-539rdchabu0kgip35aotehjskip85dj4.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
    }

    fun requestSignIn() {
        activity.startActivityForResult(googleSignInClient.signInIntent, REQ_CODE)
    }

    fun displayCurrentAccount(binding: IncludeAccountInfoIoBinding) {
        val currentAccount = auth.currentUser
        if (currentAccount != null) {
            binding.apply {
                accountNameTxt.setTextWithVisibility(currentAccount.displayName)
                accountEmailTxt.setTextWithVisibility(currentAccount.email)
                accountTypeTxt.text = "Đang phát triển..."
                currentAccount.photoUrl?.let {
                    Glide.with(activity).load(it) to accountAvatarImg
                    accountAvatarImg.toggleVisibility(true)
                }

                accountIOBtn.apply {
                    text = "Đăng xuất"
                }
            }
        } else {
            binding.apply {
                accountNameTxt.setTextWithVisibility("")
                accountEmailTxt.setTextWithVisibility("")
                accountTypeTxt.text = "Chưa đăng nhập"
                accountAvatarImg.toggleVisibility(false)

                accountIOBtn.apply {
                    text = "Đăng nhập"
                }
            }
        }

    }
}