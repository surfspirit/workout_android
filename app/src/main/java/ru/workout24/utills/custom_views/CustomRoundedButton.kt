package ru.workout24.utills.custom_views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ru.workout24.R

class CustomRoundedButton : ConstraintLayout {

    private val progress: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.progress)
    }

    private val text: TextView by lazy {
        findViewById<TextView>(R.id.txt_text)
    }

    private val icon: ImageView by lazy {
        findViewById<ImageView>(R.id.img_icon)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    )
            : super(context, attrs, defStyleAttr) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomRoundedButton)
        val text = typedArray.getString(R.styleable.CustomRoundedButton_textButton)
        val image = typedArray.getDrawable(R.styleable.CustomRoundedButton_src)
        if (text != null) {
            this.text.text = text
        }
        if (image != null) {
            this.icon.setImageDrawable(image)
        }
        typedArray.recycle()
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomRoundedButton)
        val text = typedArray.getString(R.styleable.CustomRoundedButton_textButton)
        val image = typedArray.getDrawable(R.styleable.CustomRoundedButton_src)

        if (text != null) {
            this.text.text = text
        }
        if (image != null) {
            this.icon.setImageDrawable(image)
        }

        typedArray.recycle()
    }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.custom_rounded_button, this, true)

    }

    fun showProgress(value: Boolean = true) {
        if (value) {
            progress.visibility = View.VISIBLE
            text.visibility = View.INVISIBLE
            icon.visibility = View.INVISIBLE
        } else {
            progress.visibility = View.GONE
            text.visibility = View.VISIBLE
            icon.visibility = View.VISIBLE
        }
    }
}