package ru.workout24.ui.sub_layer.subscription


import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.View

import ru.workout24.R
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_sure_not_subscribe.*

/**
 * A simple [Fragment] subclass.
 *
 */
class SureNotSubscribeFragment : BaseFragment(R.layout.fragment_sure_not_subscribe), View.OnClickListener  {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvSkip -> {
                pref.sub_passed = true
                controller.navigate(R.id.action_global_anket_layer)
            }
            R.id.btnBack -> {
                controller.popBackStack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spannable = SpannableString(tvTitle.text.toString().toUpperCase())
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.red_light)),
            0, 8,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        tvTitle.text = spannable
        btnBack.setOnClickListener(this)
        tvSkip.setOnClickListener(this)
    }

}
