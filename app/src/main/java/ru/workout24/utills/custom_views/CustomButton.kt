package ru.workout24.utills.custom_views

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ru.workout24.R
import org.jetbrains.anko.backgroundDrawable

class CustomButton : ConstraintLayout {

    private var clickCallback: (v: View) -> Unit = {}

    var isEnable = true
        set(value) {
            if (value) {
                rootView.backgroundDrawable = getBg(R.color.orange)
            } else {
                if (!isTransparentDisabled)
                    rootView.backgroundDrawable = getBg(R.color.darkGray)
                else {
                    rootView.backgroundDrawable = getBg(R.color.transparentWhite40)
                }
            }
            field = value
        }

    private var isRounded = false
    private var isTransparentDisabled = false

    private val rootView: ConstraintLayout by lazy {
        findViewById<ConstraintLayout>(R.id.root_view)
    }

    private val progress: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.progress)
    }

    private val text: TextView by lazy {
        findViewById<TextView>(R.id.txt_text)
    }

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
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomButton)
        val text = typedArray.getString(R.styleable.CustomButton_textBtn)
        isRounded = typedArray.getBoolean(R.styleable.CustomButton_isRounded, false)
        isTransparentDisabled = typedArray.getBoolean(R.styleable.CustomButton_transparentDisabled, false)
        val robotoLight = typedArray.getBoolean(R.styleable.CustomButton_robotoLight, false)
        if (robotoLight) {
            val typeface = ResourcesCompat.getFont(context, R.font.roboto_light)
            typeface?.let { this.text.typeface = typeface }
        }

        text?.let {
            this.text.text = it
        }

        rootView.backgroundDrawable = getBg(R.color.orange)

        isEnable = typedArray.getBoolean(R.styleable.CustomButton_enable, true)

        typedArray.recycle()
    }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.custom_button, this, true)
    }

    private fun getBg(color: Int): GradientDrawable {
        val bg = GradientDrawable()
        bg.setColor(ContextCompat.getColor(context, color))
        if (isRounded) bg.cornerRadius =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, context.resources.displayMetrics)

        return bg
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isEnable) return super.onTouchEvent(event)
        return false
    }

    fun setText(text: String) {
        this.text.text = text
    }

    fun makeItGray(value: Boolean = true) {
        if (value) {
            rootView.backgroundDrawable = getBg(R.color.darkslategray)
        } else {
            rootView.backgroundDrawable = getBg(R.color.coral)
        }
    }

    fun showProgress(value: Boolean) {
        if (value) {
            progress.visibility = View.VISIBLE
            text.visibility = View.INVISIBLE
        } else {
            progress.visibility = View.GONE
            text.visibility = View.VISIBLE
        }
    }

    fun getText():String
    {
        return this.text.text.toString()
    }
}