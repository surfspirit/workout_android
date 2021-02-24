package ru.workout24.ui.workout_diary.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.collapsiblecalendarview.widget.CollapsibleCalendar

class NoClickableCalendar @JvmOverloads constructor(
    context: Context,
    attr: AttributeSet? = null
) : CollapsibleCalendar(context, attr) {
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }
}