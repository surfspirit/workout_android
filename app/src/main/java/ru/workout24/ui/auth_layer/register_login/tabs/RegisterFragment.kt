package ru.workout24.ui.auth_layer.register_login.tabs

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

import ru.workout24.R
import ru.workout24.ui.auth_layer.register_login.VMRegisterLogin
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.custom_views.CustomButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_register.*
import org.jetbrains.anko.find
import ru.workout24.ui.auth_layer.register_login.LoginFieldsAutoFill


class RegisterFragment : BaseFragment(R.layout.fragment_register), View.OnClickListener {
    private val viewModel: VMRegisterLogin by lazy {
        attachViewModel<VMRegisterLogin>(
            VMRegisterLogin::class.java, true
        )
        { code, message ->
            when (code) {
                VMRegisterLogin.SUCCESS_REGISTER_CODE -> {
                    LoginFieldsAutoFill.postLoginData(viewModel.registerBody)
                    showInfoAlert(
                        context!!,
                        "Регистрация",
                        "На указанный email отправлено письмо для подтверждения регистрации",
                        "OK",
                        {}).show()
                    view?.find<CustomButton>(R.id.btn_register)?.showProgress(false)
                }
                VMRegisterLogin.ERROR_WRONG_EMAIL_CODE -> {
                    showInfoAlert(context!!, "Ошибка", message, "OK", {}).show()
                    view?.find<TextInputLayout>(R.id.til_text_input_email)?.error = message
                    view?.find<TextInputEditText>(R.id.text_input_email)
                        ?.setTextColor(resources.getColor(R.color.red_light))
                    view?.find<CustomButton>(R.id.btn_register)?.showProgress(false)
                }
                VMRegisterLogin.ERROR_WRONG_PASSWORD_CODE -> {
                    showInfoAlert(context!!, "Ошибка", message, "OK", {}).show()
                    view?.find<TextInputLayout>(R.id.til_text_input_password)?.error = message
                    view?.find<TextInputEditText>(R.id.text_input_password)
                        ?.setTextColor(resources.getColor(R.color.red_light))
                    view?.find<CustomButton>(R.id.btn_register)?.showProgress(false)
                }
                VMRegisterLogin.ERROR_EXIST_USER_CODE -> {
                    showInfoAlert(
                        context!!,
                        "Пользователь существует",
                        "Пользователь с таким адресом уже зарегистрирован",
                        "Ок",{}
                    ).show()
                    view?.find<CustomButton>(R.id.btn_register)?.showProgress(false)
                }

                else -> {
                    showInfoAlert(context!!, "Ошибка", message, "OK", {}).show()
                    view?.find<CustomButton>(R.id.btn_register)?.showProgress(false)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.constraint_layout -> {
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                text_input_first_name.clearFocus()
                text_input_last_name.clearFocus()
                text_input_email.clearFocus()
                text_input_password.clearFocus()
                constraint_layout.clearFocus()
            }
            R.id.btn_register -> {
                btn_register.showProgress(true)
                viewModel.register()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        text_description.movementMethod = LinkMovementMethod.getInstance()

        text_input_first_name.setOnChangeTextListener { v, t ->
            onTextChange(v, t)
        }
        text_input_last_name.setOnChangeTextListener { v, t ->
            onTextChange(v, t)
        }
        text_input_email.setOnChangeTextListener { v, t ->
            onTextChange(v, t)
        }
        text_input_password.setOnChangeTextListener { v, t ->
            onTextChange(v, t)
        }
        text_input__repeat_password.setOnChangeTextListener { v, t ->
            onTextChange(v, t)
        }
        text_input_password.transformationMethod = PasswordTransformationMethod()
        text_input__repeat_password.transformationMethod = PasswordTransformationMethod()
        btn_register.setOnClickListener(this)
        constraint_layout.setOnClickListener(this)
    }

    private fun onTextChange(view: View, text: CharSequence?) {
        val firstName = text_input_first_name.text
        val lastName = text_input_last_name.text
        val email = text_input_email.text
        val password = text_input_password.text.toString()
        val repeat_pass = text_input__repeat_password.text.toString()
        btn_register.isEnable =
            !(firstName!!.isEmpty() || lastName!!.isEmpty() || email!!.isEmpty() || password!!.isEmpty()|| repeat_pass!!.isEmpty()||password!=repeat_pass)

        viewModel.setRegisterBody(email, password, firstName, lastName)

        when (view.id) {
            R.id.text_input_email -> {
                til_text_input_email.error = null
                text_input_email.setTextColor(resources.getColor(R.color.white))
            }
            R.id.text_input_password -> {
                til_text_input_password.error = null
                text_input_password.setTextColor(resources.getColor(R.color.white))
            }
        }
    }

    private fun EditText.setOnChangeTextListener(callback: (view: View, text: CharSequence?) -> Unit) {
        val view = this
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
                callback(view, text)
            }
        })
    }

    companion object {
        fun newInstance(): RegisterFragment {
            val fragment = RegisterFragment()

            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }
}
