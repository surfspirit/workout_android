package ru.workout24.features

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.toast
import ru.workout24.R
import ru.workout24.network.*
import ru.workout24.push.data.DeviceIdDto
import ru.workout24.utills.Preferences
import ru.workout24.utills.base.BaseFragment

class SocialLoginFeature(val context: Context, val api: Api, val pref: Preferences) {
    private var successCallback: (User) -> Unit = {}
    private var tokenCallback: (String, SOCIAL_TYPE) -> Unit = { v, a -> }
    private var errorTokenCallback: (String, SOCIAL_TYPE) -> Unit = { v, a -> }
    var needLogin = true

    private var _googleSignInClient: GoogleSignInClient? = null

    fun createSocialLogin(fragment: BaseFragment, type: SOCIAL_TYPE) {
        val socialName: String
        val action: () -> Unit
        when (type) {
            SOCIAL_TYPE.VK -> {
                socialName = "VK"
                action = {
                    VK.login(
                        fragment.requireActivity(),
                        arrayListOf(
                            VKScope.EMAIL,
                            VKScope.OFFLINE,
                            VKScope.PHOTOS,
                            VKScope.STATS,
                            VKScope.STATUS
                        )
                    )
                }
            }
            SOCIAL_TYPE.FB -> {
                socialName = "Facebook"
                action = {
                    LoginManager.getInstance().logInWithReadPermissions(
                        fragment,
                        arrayListOf("email")
                    )
                }
            }
            SOCIAL_TYPE.GOOGLE -> {
                socialName = "Google"
                action = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestServerAuthCode(fragment.resources.getString(R.string.client_id))
                        .requestEmail()
                        .requestProfile()
                        .build()

                    _googleSignInClient = GoogleSignIn.getClient(fragment.requireContext(), gso)

                    val signInIntent = _googleSignInClient?.signInIntent
                    fragment.startActivityForResult(
                        signInIntent,
                        RC_SIGN_IN
                    )
                }
            }
        }
        fragment.showActionAlert(
            fragment.requireContext(),
            "Вход",
            "Программа «WorkoutGeneration» хочет открыть «$socialName»",
            "Открыть",
            "Отменить",
            {
                action()
            },
            {
                errorTokenCallback("", SOCIAL_TYPE.FB)
            }
        ).show()
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            //GOOGLE RESULT
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleActivityResult(
                task
            )
        } else if (data != null) {
            //FACEBOOK RESULT
            val callback = CallbackManager.Factory.create()
            LoginManager.getInstance().registerCallback(callback,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        login(
                            loginResult.accessToken.token,
                            SOCIAL_TYPE.FB
                        )
                    }

                    override fun onCancel() {
                        errorTokenCallback("", SOCIAL_TYPE.FB)
                        context.toast("Регистрация через Facebook отменена")
                    }

                    override fun onError(exception: FacebookException) {
                        errorTokenCallback("", SOCIAL_TYPE.FB)
                        context.toast(
                            context.resources.getString(R.string.auth_fb_error, exception.message)
                        )
                    }
                })
            callback.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun handleVkResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                login(
                    token.accessToken,
                    SOCIAL_TYPE.VK
                )
            }

            override fun onLoginFailed(errorCode: Int) {
                errorTokenCallback("", SOCIAL_TYPE.VK)
                context.toast(
                    context.resources.getString(
                        R.string.auth_vk_error,
                        errorCode.toString()
                    )
                )
            }
        }
        data?.let { VK.onActivityResult(requestCode, resultCode, data, callback) }
    }

    private fun handleActivityResult(completedTask: Task<GoogleSignInAccount>) {
        if (completedTask.isSuccessful) {
            try {
                val account = completedTask.getResult(ApiException::class.java)
                val body = GoogleAccessTokenBody(
                    grant_type = "authorization_code",
                    client_id = context.resources.getString(R.string.client_id),
                    client_secret = context.resources.getString(R.string.client_secret),
                    code = account?.serverAuthCode
                )
                api.getAccessTokenGoogle(
                    body,
                    "https://www.googleapis.com/oauth2/v4/token"
                ).observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess { response ->
                        response.access_token?.let {
                            login(
                                it,
                                SOCIAL_TYPE.GOOGLE
                            )
                        }
                    }
                    .doOnError {
                        context.toast("Не удалось авторизоваться через соц.сеть")
                    }
                    .subscribe()
            } catch (e: ApiException) {
                errorTokenCallback("", SOCIAL_TYPE.GOOGLE)
                context.toast(
                    context.resources.getString(
                        R.string.auth_google_error,
                        e.localizedMessage
                    )
                )
            }
        } else {
            errorTokenCallback("", SOCIAL_TYPE.GOOGLE)
            completedTask.exception?.localizedMessage?.let { message -> context.toast(message) }
        }
    }

    @SuppressLint("CheckResult")
    private fun login(token: String, type: SOCIAL_TYPE) {
        tokenCallback(token, type)
        if (needLogin) {
            val tokenBody = CreatePushTokenBody(token)
            val request = when (type) {
                SOCIAL_TYPE.VK -> api.createTokenVk(tokenBody)
                SOCIAL_TYPE.FB -> api.createTokenFacebook(tokenBody)
                SOCIAL_TYPE.GOOGLE -> api.createTokenGoogle(tokenBody)
            }
            request
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    if (it.isSuccessful) {
                        it.data
                    } else null
                }
                .subscribe({ responce ->
                    responce?.let {
                        pref.authToken = responce.token
                        pref.userProfileId = responce.user.id
                        api.registerDevice(DeviceIdDto(pref.pushToken!!)).subscribeOn(
                            Schedulers.io()
                        ).subscribe({
                            it
                        }, {
                            it
                        })
                        successCallback(responce.user)
                    }
                }, {
                    context.toast("Не удалось получить профиль пользователя")
                })
        }
    }

    fun setSuccessCallback(callback: (User) -> Unit) {
        successCallback = callback
    }

    fun setTokenCallback(callback: (String, SOCIAL_TYPE) -> Unit) {
        tokenCallback = callback
    }

    fun setErrorTokenCallback(callback: (String, SOCIAL_TYPE) -> Unit) {
        errorTokenCallback = callback
    }

    fun logout() {
        // логаут сессии в фейсбуке
        LoginManager.getInstance().logOut()
        // логаут сессии в вк
        if (VK.isLoggedIn()) VK.logout()
        // логаут сессии в google client
        _googleSignInClient?.signOut()
    }

    companion object {
        private const val RC_SIGN_IN = 999
    }
}

enum class SOCIAL_TYPE {
    FB, VK, GOOGLE
}