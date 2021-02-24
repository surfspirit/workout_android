package ru.workout24.ui.auth_layer.register_login.tabs


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.lifecycle.Observer
import ru.workout24.R
import ru.workout24.ui.auth_layer.register_login.VMRegisterLogin
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.custom_views.CustomButton
import ru.workout24.utills.custom_views.CustomRoundedButton
import ru.workout24.utills.setTextChangeListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.find
import ru.workout24.features.SOCIAL_TYPE
import ru.workout24.features.SocialLoginFeature
import ru.workout24.network.User
import ru.workout24.ui.auth_layer.register_login.LoginFieldsAutoFill
import javax.inject.Inject

class LoginFragment : BaseFragment(R.layout.fragment_login), View.OnClickListener {
    @Inject
    lateinit var socialLoginFeature: SocialLoginFeature

    private val viewModel: VMRegisterLogin by lazy {
        attachViewModel<VMRegisterLogin>(
            VMRegisterLogin::class.java
        )
        { code, message ->
            when (code) {
                VMRegisterLogin.ERROR_WRONG_EMAIL_CODE -> {
                    showInfoAlert(context!!, "Ошибка", message, "OK", {}).show()
                    view?.find<TextInputLayout>(R.id.til_text_input_email)?.error = message
                    view?.find<TextInputEditText>(R.id.text_input_email)
                        ?.setTextColor(resources.getColor(R.color.red_light))
                    view?.find<CustomButton>(R.id.btn_login)?.showProgress(false)
                }
                VMRegisterLogin.ERROR_WRONG_PASSWORD_CODE -> {
                    showInfoAlert(context!!, "Ошибка", message, "OK", {}).show()
                    view?.find<TextInputLayout>(R.id.til_text_input_password)?.error = message
                    view?.find<TextInputEditText>(R.id.text_input_password)
                        ?.setTextColor(resources.getColor(R.color.red_light))
                    view?.find<CustomButton>(R.id.btn_login)?.showProgress(false)
                }
                VMRegisterLogin.ERROR_SOCIAL -> {
                    showInfoAlert(context!!, "Ошибка", message, "OK", {}).show()
                    view?.find<CustomButton>(R.id.btn_login)?.showProgress(false)
                    view?.find<CustomRoundedButton>(R.id.login_facebook)?.showProgress(false)
                    view?.find<CustomRoundedButton>(R.id.login_vk)?.showProgress(false)
                    view?.find<CustomRoundedButton>(R.id.login_google)?.showProgress(false)
                }
                VMRegisterLogin.SUCCESS_LOGIN_CODE -> {
                    checkUserObject(viewModel.currentUser!!)
                }
                else -> {
                    showInfoAlert(context!!, "Ошибка", message, "OK", {}).show()
                    view?.find<CustomButton>(R.id.btn_login)?.showProgress(false)
                    view?.find<CustomRoundedButton>(R.id.login_facebook)?.showProgress(false)
                    view?.find<CustomRoundedButton>(R.id.login_vk)?.showProgress(false)
                    view?.find<CustomRoundedButton>(R.id.login_google)?.showProgress(false)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.constraint_layout -> {
                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                text_input_email.clearFocus()
                text_input_password.clearFocus()
                constraint_layout.clearFocus()
            }
            R.id.btn_login -> {
                btn_login.showProgress(true)
                viewModel.login()
            }
            R.id.txt_forgot_pass -> {
                controller.navigate(R.id.action_registerLoginFragment2_to_forgotPasswordFragment2)
            }
            R.id.login_facebook -> {
                socialLoginFeature.createSocialLogin(this, SOCIAL_TYPE.FB)
            }
            R.id.login_vk -> {
                socialLoginFeature.createSocialLogin(this, SOCIAL_TYPE.VK)
            }
            R.id.login_google -> {
                socialLoginFeature.createSocialLogin(this, SOCIAL_TYPE.GOOGLE)
            }
        }
    }

    fun onTextChange(view: EditText, text: CharSequence?) {
        when (view.id) {
            text_input_email.id -> {
                viewModel.setEmail(text)
                til_text_input_email.error = null
                text_input_email.setTextColor(resources.getColor(R.color.white))

                btn_login.isEnable =
                    !(text_input_email.text!!.isEmpty() || text_input_password.text!!.isEmpty())
            }
            text_input_password.id -> {
                viewModel.setPass(text)
                til_text_input_password.error = null
                text_input_password.setTextColor(resources.getColor(R.color.white))

                btn_login.isEnable =
                    !(text_input_email.text!!.isEmpty() || text_input_password.text!!.isEmpty())
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        text_input_email.setTextChangeListener { view, text -> onTextChange(view, text) }
        text_input_password.setTextChangeListener { view, text -> onTextChange(view, text) }
        text_input_password.transformationMethod = PasswordTransformationMethod()
        txt_forgot_pass.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        login_facebook.setOnClickListener(this)
        login_vk.setOnClickListener(this)
        login_google.setOnClickListener(this)
        constraint_layout.setOnClickListener(this)

        socialLoginFeature.setSuccessCallback { user ->
            checkUserObject(user)
        }

        LoginFieldsAutoFill.getLoginAutoFillFields().observe(this, Observer { fieldsData ->
            text_input_email.setText(fieldsData.email)
            text_input_password.setText(fieldsData.password)
        })
    }

    private fun checkUserObject(user: User) {
        pref.notify_passed = user.gender != "NONE" && !user.bdate.isNullOrEmpty()
        pref.gender_date_selected = pref.notify_passed
        pref.anket_passed = user.goals != "NONE"
        pref.test_passed = user.completedTest == true || user.skip_test == true
        when {
            user.gender == "NONE" -> {
                if (pref.notify_passed != true) {
                    controller.navigate(R.id.action_global_applyNotifFragment2)
                } else {
                    controller.navigate(R.id.action_global_genderChoiceFragment)
                }
            }
            user.goals == "NONE" -> controller.navigate(R.id.action_global_anket_layer2)
            user.completedTest != true || user.skip_test != true -> controller.navigate(R.id.action_global_menu_layer2)
        }
    }

    companion object {
        fun newInstance(): LoginFragment {
            val fragment = LoginFragment()

            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        socialLoginFeature.handleActivityResult(requestCode, resultCode, data)
    }
}
