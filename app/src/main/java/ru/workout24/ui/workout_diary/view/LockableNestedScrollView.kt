package ru.workout24.ui.workout_diary.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class LockableNestedScrollView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : NestedScrollView(context, attributeSet) {
    var isLocked = false

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            !isLocked && super.onTouchEvent(ev)
        }
        return super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return !isLocked && super.onInterceptTouchEvent(ev)
    }
}