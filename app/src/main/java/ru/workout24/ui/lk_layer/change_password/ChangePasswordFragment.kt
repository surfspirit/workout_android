package ru.workout24.ui.lk_layer.change_password

import android.os.Bundle
import android.text.TextWatcher
import android.view.View
import ru.workout24.R
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_change_password.*
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import ru.workout24.network.ChangePasswordBody


class ChangePasswordFragment : BaseFragment(R.layout.fragment_change_password) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val changePassword = resourceFactory.changePassword
        appBarLayout.setOnBackClick {
            hideKeyboard()
            controller.popBackStack()
        }
        textInputLayout.error = null
        edit_password.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                val t = s.toString()
                if (t.length < 8) {
                    textInputLayout.error = "Минимальная длина пароля 8 символов"
                } else if (!t.matches(Regex("[a-zA-Z0-9]*"))) {
                    textInputLayout.error = "Пароль должен состоять из английский букв и цифр"
                } else {
                    textInputLayout.error = null
                }

            }

        })
        textInputLayout2.error = null
        edit_password2.addTextChangedListener(object : TextWatcher {

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                val t = s.toString()
                if (!t.equals(edit_password.text.toString())) {
                    textInputLayout2.error = "Пароли не совпадают"
                } else {
                    textInputLayout2.error = null
                }

            }

        })
        btn_save.setOnClickListener {
            if (textInputLayout.error == null &&
                textInputLayout2.error == null &&
                edit_password.text.toString().isNotEmpty() &&
                edit_password2.text.toString().isNotEmpty()
            ) {
                val query = ChangePasswordBody(
                    edit_password3.text.toString(),
                    edit_password.text.toString()
                )
                changePassword.clear()
                changePassword.query = query
                changePassword.load()
            } else {
                showInfoAlert(
                    context!!,
                    "Изменение пароля",
                    "Пожалуйста, корректно заполните поля",
                    "Хорошо",
                    {}).show()
            }

        }
        changePassword.onChange(this, {
            showInfoAlert(context!!, "Изменение пароля", "Пароль успешно изменен", "Хорошо", {
                hideKeyboard()
                controller.popBackStack()
            }).show()
        }, {
            if (!it.equals("Неверные логин или пароль"))
                showInfoAlert(context!!, "Изменение пароля", it, "Хорошо", {
                }).show()
            else
                showInfoAlert(
                    context!!,
                    "Изменение пароля",
                    "Неверный пароль",
                    "Хорошо",
                    {
                    }).show()

        })

        edit_password3.transformationMethod = AsteriskPasswordTransformationMethod()
    }
}

class AsteriskPasswordTransformationMethod : PasswordTransformationMethod() {
    override fun getTransformation(source: CharSequence, view: View): CharSequence {
        return PasswordCharSequence(source)
    }

    private inner class PasswordCharSequence(private val mSource: CharSequence)// Store char sequence
        : CharSequence {
        override val length: Int
            get() = mSource.length

        override fun get(index: Int): Char {
            return '*'
        }

        override fun subSequence(start: Int, end: Int): CharSequence {
            return mSource.subSequence(start, end) // Return default
        }
    }
};
