package ru.workout24.ui.auth_layer.register_login


import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.Observer

import ru.workout24.R
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.custom_views.CustomButton
import ru.workout24.utills.setTextChangeListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_forgot_password.*
import org.jetbrains.anko.find
import ru.workout24.utills.hideSoftKeyboard

/**
 * Экран восстановления пароля
 */

class ForgotPasswordFragment : BaseFragment(R.layout.fragment_forgot_password), View.OnClickListener {

    private val viewModel: VMForgotPassword by lazy {
        attachViewModel<VMForgotPassword>(
            VMForgotPassword::class.java, true
        )
        { code, message ->
            when (code) {
                VMForgotPassword.SUCCESS_CODE -> {
                    showInfoAlert(
                        context!!,
                        "Восстановление пароля",
                        "На указанный email отправлено письмо для восстановления пароля",
                        "OK",
                        {
                            controller.popBackStack()
                        }).show()
                    view?.find<CustomButton>(R.id.btn_restore_password)?.showProgress(false)
                }
                VMForgotPassword.ERROR_WRONG_EMAIL_CODE -> {
                    showInfoAlert(
                        context!!,
                        "Неверный email",
                        "Пожалуйста, проверьте свой email",
                        "OK",
                        {}).show()
                    view?.find<TextInputLayout>(R.id.til_text_input_email)?.error = "Неверный email"
                    view?.find<TextInputEditText>(R.id.text_input_email)
                        ?.setTextColor(resources.getColor(R.color.red_light))
                    view?.find<CustomButton>(R.id.btn_restore_password)?.showProgress(false)
                }

                else -> {
                    showInfoAlert(context!!, "Ошибка", message, "OK", {}).show()
                    view?.find<CustomButton>(R.id.btn_restore_password)?.showProgress(false)
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.constraint_layout -> {
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                text_input_email.clearFocus()
                constraint_layout.clearFocus()
            }
            R.id.btn_restore_password -> {
                btn_restore_password.showProgress(true)
                viewModel.sendEmail()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().hideSoftKeyboard()

        appBarLayout.setOnBackClick {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(constraint_layout.windowToken, 0)
            text_input_email.clearFocus()
            constraint_layout.clearFocus()
            controller.popBackStack()
        }
        appBarLayout.hidePic1Pic2()

        btn_restore_password.setOnClickListener(this)
        constraint_layout.setOnClickListener(this)

        viewModel.email.observe(this, Observer {
            it?.let { email ->
                btn_restore_password.isEnable = email.isNotEmpty()
            }
        })

        text_input_email.setTextChangeListener { _, text ->
            run {
                viewModel.email.value = text.toString()
                til_text_input_email.error = null
                text_input_email.setTextColor(resources.getColor(R.color.white))
            }
        }
    }
}
