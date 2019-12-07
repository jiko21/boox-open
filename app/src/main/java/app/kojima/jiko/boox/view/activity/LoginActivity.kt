package app.kojima.jiko.boox.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import app.kojima.jiko.boox.R
import app.kojima.jiko.boox.model.services.firestore.UserService
import app.kojima.jiko.boox.view.view.ProgressCircle
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager
    private lateinit var progressCircle: ProgressCircle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)

        setContentView(R.layout.activity_login)
        policyTextView.movementMethod = LinkMovementMethod.getInstance()
        // Twitter
        val provider = OAuthProvider.newBuilder("twitter.com")
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.currentUser?.also {
            goToMainPage()
        }
        callbackManager = CallbackManager.Factory.create()
        val loginManager = LoginManager.getInstance()
        loginManager
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    handleFacebookAccessToken(result.accessToken)
                }

                override fun onCancel() {
                    progressCircle.dismiss()
                }

                override fun onError(error: FacebookException?) {
                    progressCircle.dismiss()
                    AlertDialog.Builder(applicationContext)
                        .setTitle("エラー")
                        .setMessage("申し訳ございません。Facebookログインにおいてエラーが生じました。管理者にお問い合わせください。${error?.localizedMessage}")
                        .setPositiveButton("OK", null)
                        .show()
                }
            })
        facebookButton.setOnClickListener {
            loginManager.logOut()
            loginManager.logIn(this, arrayListOf())
            progressCircle = ProgressCircle()
            progressCircle.show(supportFragmentManager, "")
        }
        val pendingResultTask = firebaseAuth.pendingAuthResult
        pendingResultTask?.also {
            progressCircle = ProgressCircle()
            progressCircle.show(supportFragmentManager, "")
            handleTask(it)
        }
        twitterButton.setOnClickListener {
            val task = firebaseAuth.startActivityForSignInWithProvider(this, provider.build())
            progressCircle = ProgressCircle()
            progressCircle.show(supportFragmentManager, "")
            handleTask(task)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (FacebookSdk.isFacebookRequestCode(requestCode)) {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        val task = firebaseAuth.signInWithCredential(credential)
        handleTask(task)
    }

    private fun handleTask(task: Task<AuthResult>) {
        task.addOnSuccessListener {
            authSuccess(it)
        }
        .addOnFailureListener {
            progressCircle.dismiss()
            AlertDialog.Builder(this)
                .setTitle("エラー")
                .setMessage("申し訳ございません。ログインに失敗しました。${it.localizedMessage}")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun authSuccess(result: AuthResult) {
        val user = result.user ?: return@authSuccess
        if (result.additionalUserInfo!!.isNewUser) {
            signUp(user)
        } else {
            progressCircle.dismiss()
            goToMainPage()
        }
    }

    private fun signUp(user: FirebaseUser) {
        UserService.createUser(user)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : CompletableObserver {
                override fun onComplete() {
                    progressCircle.dismiss()
                    goToMainPage()
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    progressCircle.dismiss()
                    AlertDialog.Builder(applicationContext)
                        .setTitle("エラー")
                        .setMessage("申し訳ございません。ログインに失敗しました。${e.localizedMessage}")
                        .setPositiveButton("OK", null)
                        .show()
                }
            })
    }

    private fun goToMainPage() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
