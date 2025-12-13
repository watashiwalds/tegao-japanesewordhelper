package com.tegaoteam.application.tegao.ui.options.account

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.tegaoteam.application.tegao.databinding.IncludeAccountInfoIoBinding
import com.tegaoteam.application.tegao.utils.AppToast
import com.tegaoteam.application.tegao.utils.setTextWithVisibility
import com.tegaoteam.application.tegao.utils.toggleVisibility
import timber.log.Timber

//todo: Really investigate in a new way to handle account credentials
@Suppress("DEPRECATION")
class SignInHelper(private val activity: AppCompatActivity, private val launcher: ActivityResultLauncher<Intent>, private val binding: IncludeAccountInfoIoBinding) {

    private var googleSignInClient: GoogleSignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("135658289710-539rdchabu0kgip35aotehjskip85dj4.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, gso)
        displayCurrentAccount()
    }

    fun requestSignIn() {
        googleSignInClient.signOut().addOnCompleteListener {
            launcher.launch(googleSignInClient.signInIntent)
        }
    }

    fun processSignInResult(res: ActivityResult): String? {
        var idToken: String? = null
        val task = GoogleSignIn.getSignedInAccountFromIntent(res.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            auth.signInWithCredential(credential).addOnCompleteListener { t ->
                if (t.isSuccessful) {
                    auth.currentUser?.getIdToken(true)?.addOnSuccessListener { result ->
                        val token = result.token
                        if (token != null) {
                            idToken = token
                        } else {
                            AppToast.show("Failed to get token credential from Firebase", AppToast.LENGTH_SHORT)
                        }
                        displayCurrentAccount()
                    }
                } else {
                    AppToast.show("Failed to login when connecting to Firebase", AppToast.LENGTH_SHORT)
                }
            }
        } catch (e: ApiException) {
            Timber.e(e)
            AppToast.show("Error when login: ${e.message}", AppToast.LENGTH_SHORT)
        }
        return idToken
    }

    private fun displayCurrentAccount() {
        val currentAccount = auth.currentUser
        if (currentAccount != null) {
            binding.apply {
                loLoggingPgb.toggleVisibility(false)

                accountNameTxt.setTextWithVisibility(currentAccount.displayName)
                accountEmailTxt.setTextWithVisibility(currentAccount.email)
                accountTypeTxt.setTextWithVisibility("Đang phát triển...")
                currentAccount.photoUrl?.let {
                    Glide.with(activity).load(it) to accountAvatarImg
                    accountAvatarImg.toggleVisibility(true)
                }

                accountIOBtn.apply {
                    text = "Đăng xuất"
                    setOnClickListener {
                        auth.signOut()
                        displayCurrentAccount()
                    }
                }
            }
        } else {
            binding.apply {
                loLoggingPgb.toggleVisibility(false)

                accountNameTxt.setTextWithVisibility("")
                accountEmailTxt.setTextWithVisibility("")
                accountTypeTxt.setTextWithVisibility("Chưa đăng nhập")
                accountAvatarImg.toggleVisibility(false)

                accountIOBtn.apply {
                    text = "Đăng nhập"
                    setOnClickListener {
                        accountTypeTxt.toggleVisibility(false)
                        loLoggingPgb.toggleVisibility(true)
                        requestSignIn()
                    }
                }
            }
        }

    }
}