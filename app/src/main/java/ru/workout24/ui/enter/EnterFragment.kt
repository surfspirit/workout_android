package ru.workout24.ui.enter


import android.os.Bundle
import android.view.View
import ru.workout24.R
import ru.workout24.ui.auth_layer.register_login.RegisterLoginFragment
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_enter.*

/**
 * Экран развилки входа и регистрации
 */

class EnterFragment : BaseFragment(R.layout.fragment_enter) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_login.setOnClickListener {
            controller.navigate(
                R.id.action_enterFragment_to_auth_layer,
                RegisterLoginFragment.getBundle(1)
            )
        }
        btn_register.setOnClickListener {
            controller.navigate(
                R.id.action_enterFragment_to_auth_layer,
                RegisterLoginFragment.getBundle(0)
            )
        }

    }
}
