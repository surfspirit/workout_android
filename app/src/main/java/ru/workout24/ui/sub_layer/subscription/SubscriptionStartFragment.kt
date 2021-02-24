package ru.workout24.ui.sub_layer.subscription


import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.view.isVisible

import ru.workout24.R
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_apply_notifications.*

/**
 * A simple [Fragment] subclass.
 *
 */
class SubscriptionStartFragment : BaseFragment(R.layout.fragment_apply_notifications), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_skip -> {
                controller.navigate(R.id.action_subscriptionStartFragment_to_sureNotSubscribeFragment)
            }
            R.id.btn_get -> {
                controller.navigate(R.id.action_subscriptionStartFragment_to_chooseSubscriptionFragment)
            }
            R.id.btnQuestion -> {
                controller.navigate(R.id.action_subscriptionStartFragment_to_subscriptionPagerFragment)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnQuestion.isVisible = true

        val spannable = SpannableString("WG подписка")
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.red_light)),
            0, 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text_title.text = spannable

        text_description.text = "Подписка открывает доступ ко всем тренировкам и к индивидуальной программе Workout Generation, адаптированной под ваши цели и уровень подготовки"
        btn_skip.text = "Подписаться позже"
        btn_get.text = "Подписаться"

        btn_skip.setOnClickListener(this)
        btn_get.setOnClickListener(this)
        btnQuestion.setOnClickListener(this)
    }
}
