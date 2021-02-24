package ru.workout24.utills.custom_views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import ru.workout24.R

class CustomExercisesCounter : ConstraintLayout {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {

        init(context, attrs, defStyleAttr)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr) {

        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int? = null) {
        /*
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomButton)
        val text = typedArray.getString(R.styleable.CustomButton_textBtn)

        typedArray.recycle()
        */
    }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.custom_exercises_counter, this, true)
    }
}