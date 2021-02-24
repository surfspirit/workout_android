package ru.workout24.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Spinner


class CustomSpinner(context: Context, attrs: AttributeSet) : Spinner(context, attrs) {
    private var listener: OnItemSelectedListener? = null

    override fun setSelection(position: Int) {
        super.setSelection(position)
        if (listener != null)
            listener!!.onItemSelected(this, getChildAt(position), position, 0);
    }

    fun setOnItemSelectedEvenIfUnchangedListener(
        listener: OnItemSelectedListener
    ) {
        this.listener = listener
    }
}