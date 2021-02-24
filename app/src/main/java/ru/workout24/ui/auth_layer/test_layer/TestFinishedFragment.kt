package ru.workout24.ui.auth_layer.test_layer


import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View

import ru.workout24.R
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.base.OnBackPressedListener
import kotlinx.android.synthetic.main.fragment_test_finished.*
import ru.workout24.network.ResourceProvider
import ru.workout24.utills.levels
import javax.inject.Inject

class TestFinishedFragment : BaseFragment(R.layout.fragment_test_finished), View.OnClickListener,
    OnBackPressedListener {
    @Inject
    lateinit var resProvider: ResourceProvider

    private val userRes by lazy {
        resProvider.userResource
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {
                pref.test_passed = true
                try {
                    controller.navigate(R.id.action_global_menu_layer)
                } catch (e: Exception) {
                    controller.popBackStack(R.id.startTestFragment, true)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userRes.load()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userRes.onChange(viewLifecycleOwner, { user ->
            your_level.text = "Ваш уровень: ${levels[user?.trainingLevel]}"
        })

        val spannable = SpannableString(text_title.text.toString().toUpperCase())
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.red_light)),
            0, 11,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text_title.text = spannable
        btn_next.setOnClickListener(this)
    }

    override fun onBackPressed() {
    }
}