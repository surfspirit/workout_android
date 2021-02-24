package ru.workout24.ui.auth_layer.anket_layer.final_part


import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.TextView

import ru.workout24.R
import ru.workout24.ui.auth_layer.anket_layer.AnketViewModel
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.custom_views.RadioGroupConstraintLayout
import kotlinx.android.synthetic.main.fragment_training_level.*
import android.graphics.Typeface
import android.text.TextPaint
import android.text.style.TypefaceSpan
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat


class TrainingLevelFragment : BaseFragment(R.layout.fragment_training_level), View.OnClickListener {

    private val viewModel: AnketViewModel by lazy {
        attachViewModel<AnketViewModel>(
            AnketViewModel::class.java, false
        )
        { code, message ->

        }
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_next -> {
                controller.navigate(R.id.action_placeholder_to_trainingGoalFragment)
            }
            R.id.c_low -> {
                radio_group.check(R.id.rb_low)
            }
            R.id.c_beginner -> {
                radio_group.check(R.id.rb_beginner)
            }
            R.id.c_medium -> {
                radio_group.check(R.id.rb_medium)
            }
            R.id.c_advanced -> {
                radio_group.check(R.id.rb_advanced)
            }
            R.id.c_master -> {
                radio_group.check(R.id.rb_master)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarLayout.setOnBackClick {

            controller.popBackStack()
        }
        btn_next.setOnClickListener(this)

        setColorToString(text_description_low, 7)
        setColorToString(text_description_beginner, 11)
        setColorToString(text_description_medium, 8)
        setColorToString(text_description_advanced, 12)
        setColorToString(text_description_master, 7)
        radio_group.setOnCheckedChangeListener { group, checkedId -> setChecked(group, checkedId) }
        c_low.setOnClickListener(this)
        c_beginner.setOnClickListener(this)
        c_medium.setOnClickListener(this)
        c_advanced.setOnClickListener(this)
        c_master.setOnClickListener(this)
        viewModel.trainingLevel.value?.let {
            radio_group.check(it)
            btn_next.isEnabled = true
        }
    }

    private fun setChecked(group: RadioGroupConstraintLayout, checkedId: Int) {
        btn_next.isEnabled = true
        when (checkedId) {
            R.id.rb_low -> {
                Log.d("setChecked", "rb_low")
            }
            R.id.rb_beginner -> {
                Log.d("setChecked", "rb_beginner")
            }
            R.id.rb_medium -> {
                Log.d("setChecked", "rb_medium")
            }
            R.id.rb_advanced -> {
                Log.d("setChecked", "rb_advanced")
            }
            R.id.rb_master -> {
                Log.d("setChecked", "rb_master")
            }
        }
        viewModel.trainingLevel.value = checkedId
    }

    private fun setColorToString(text_view: TextView, end: Int) {
        val spannable = SpannableString(text_view.text)
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.coral)),
            0, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            CustomTypefaceSpan("",ResourcesCompat.getFont(context!!, R.font.roboto)!!),
            0, end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text_view.text = spannable
    }
}

class CustomTypefaceSpan(family: String, private val newType: Typeface) : TypefaceSpan(family) {

    override fun updateDrawState(ds: TextPaint) {
        applyCustomTypeFace(ds, newType)
    }

    override fun updateMeasureState(paint: TextPaint) {
        applyCustomTypeFace(paint, newType)
    }

    private fun applyCustomTypeFace(paint: Paint, tf: Typeface) {
        val oldStyle: Int
        val old = paint.getTypeface()
        if (old == null) {
            oldStyle = 0
        } else {
            oldStyle = old!!.getStyle()
        }

        val fake = oldStyle and tf.style.inv()
        if (fake and Typeface.BOLD != 0) {
            paint.setFakeBoldText(true)
        }

        if (fake and Typeface.ITALIC != 0) {
            paint.setTextSkewX(-0.25f)
        }

        paint.setTypeface(tf)
    }
}