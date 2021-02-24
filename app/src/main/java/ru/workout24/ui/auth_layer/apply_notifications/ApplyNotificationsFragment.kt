package ru.workout24.ui.auth_layer.apply_notifications


import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View

import ru.workout24.R
import ru.workout24.ui.auth_layer.anket_layer.AnketViewModel
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_apply_notifications.*

/**
 * Экран подтверждения получения уведомлений
 */

class ApplyNotificationsFragment : BaseFragment(R.layout.fragment_apply_notifications), View.OnClickListener {

    private val viewModel: AnketViewModel by lazy {
        attachViewModel<AnketViewModel>(
            AnketViewModel::class.java, false
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_skip -> {
                viewModel.notifications.value = false
                pref.notify_passed = true
                pref.notifications = false
                controller.navigate(R.id.action_applyNotifFragment_to_genderChoiceFragment)
            }
            R.id.btn_get -> {
                viewModel.notifications.value = true
                pref.notify_passed = true
                pref.notifications = true
                controller.navigate(R.id.action_applyNotifFragment_to_genderChoiceFragment)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spannable = SpannableString(resources.getString(R.string.text_apply_notif_title))
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.red_light)),
            0, 1,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text_title.text = spannable

        btn_skip.setOnClickListener(this)
        btn_get.setOnClickListener(this)
    }
}
