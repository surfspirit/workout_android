package ru.workout24.ui.sub_layer.subscription


import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.View

import ru.workout24.R
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_test_finished.*

/**
 * A simple [Fragment] subclass.
 *
 */
class BestSubscriptionDecision : BaseFragment(R.layout.fragment_test_finished), View.OnClickListener {
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {
                pref.sub_passed = true
                controller.navigate(R.id.action_global_anket_layer)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spannable = SpannableString("Самое верное решение".toUpperCase())
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.red_light)),
            6, 12,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        btn_next.text = "Вперед"
        text_title.text = spannable
        text_description.text = "Спасибо, что оформили подписку\n" +
                "Workout Generation \n" +
                "Вы в шаге от лучшей в своей жизни тренировки"
        btn_next.setOnClickListener(this)
    }
}
