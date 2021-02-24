package ru.workout24.ui.workout_diary.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.workout24.R
import ru.workout24.ui.workout_diary.CalendarMode
import org.jetbrains.anko.backgroundDrawable
import org.jetbrains.anko.find

class CalendarSelectableView @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null
) : LinearLayout(context, attr) {
    private var isMode = CalendarMode.WEEK

    private val backgroundDrawable =
        ContextCompat.getDrawable(context, R.drawable.custom_rounded_button_bg)
    private val onClickListener = OnClickListener { view ->
        when (view.id) {
            R.id.week -> setMode(CalendarMode.WEEK, true)
            R.id.month -> setMode(CalendarMode.MONTH, true)
        }
    }
    private var modeListener: (mode: CalendarMode) -> Unit = {}

    private val week by lazy {
        find<TextView>(R.id.week)
    }

    private val month by lazy {
        find<TextView>(R.id.month)
    }

    init {
        View.inflate(context, R.layout.calendar_selectable_view, this)
        week.setOnClickListener(onClickListener)
        month.setOnClickListener(onClickListener)
    }

    fun setCalendarModeListener(listener: (mode: CalendarMode) -> Unit) {
        modeListener = listener
    }

    private fun setMode(value: CalendarMode, refresh: Boolean) {
        isMode = value
        when(value){
            CalendarMode.WEEK -> {
                week.backgroundDrawable = backgroundDrawable
                month.backgroundDrawable = null
            }
            CalendarMode.MONTH -> {
                month.backgroundDrawable = backgroundDrawable
                week.backgroundDrawable = null
            }
        }
        if (refresh) modeListener(value)
    }

    fun setInitMode(mode: CalendarMode) {
        setMode(mode, false)
    }
}