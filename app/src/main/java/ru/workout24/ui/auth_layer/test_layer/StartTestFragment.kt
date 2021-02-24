package ru.workout24.ui.auth_layer.test_layer


import android.os.Bundle
import android.view.View

import ru.workout24.R
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.setHtmlText
import kotlinx.android.synthetic.main.fragment_start_test.*

/**
 * Экран с возможностью начать тестирование
 */

class StartTestFragment : BaseFragment(R.layout.fragment_start_test) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView6.setHtmlText("Пройдите <font color='#ea463b'>фитнес-тест</font> и получите личного инструктора для более эффективного плана занятий специально для вас!")

        btn_start_test.setOnClickListener { controller.navigate(R.id.action_startTestFragment_to_testListFragment) }
    }
}