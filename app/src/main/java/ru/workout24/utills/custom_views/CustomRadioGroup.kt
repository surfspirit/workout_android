package ru.workout24.utills.custom_views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.TextView
import ru.workout24.R

class CustomRadioGroup : RadioGroupConstraintLayout {

    enum class POSITIONS {
        FIRST, SECOND, THIRD
    }

    private val txt1: TextView by lazy {
        findViewById<TextView>(R.id.txt1)
    }
    private val txt2: TextView by lazy {
        findViewById<TextView>(R.id.txt2)
    }
    private val txt3: TextView by lazy {
        findViewById<TextView>(R.id.txt3)
    }

    private val btn1: RadioButton by lazy {
        findViewById<RadioButton>(R.id.radioButton1)
    }
    private val btn2: RadioButton by lazy {
        findViewById<RadioButton>(R.id.radioButton2)
    }
    private val btn3: RadioButton by lazy {
        findViewById<RadioButton>(R.id.radioButton3)
    }
    private val radioGroup: RadioGroupConstraintLayout by lazy {
        findViewById<RadioGroupConstraintLayout>(R.id.radioGroup)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.custom_radio_group, this, true)
    }

    private fun init(context: Context, attrs: AttributeSet?) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomRadioGroup)
        val initPos = typedArray.getInteger(R.styleable.CustomRadioGroup_initPosition, 0)
        val text1 = typedArray.getString(R.styleable.CustomRadioGroup_textFirst)
        val text2 = typedArray.getString(R.styleable.CustomRadioGroup_textSecond)
        val text3 = typedArray.getString(R.styleable.CustomRadioGroup_textThird)

        when (initPos) {
            1 -> btn1.isChecked = true
            2 -> btn2.isChecked = true
            3 -> btn3.isChecked = true
        }

        text1?.let {
            txt1.text = it
            txt1.setOnClickListener { radioGroup.check(R.id.radioButton1) }
        }

        text2?.let {
            txt2.text = it
            txt2.setOnClickListener { radioGroup.check(R.id.radioButton2) }
        }

        text3?.let {
            txt3.text = it
            txt3.setOnClickListener { radioGroup.check(R.id.radioButton3) }
        }

        typedArray.recycle()
    }

    fun setInitPosition(position: POSITIONS) {
        when (position) {
            POSITIONS.FIRST -> btn1.isChecked = true
            POSITIONS.SECOND -> btn2.isChecked = true
            POSITIONS.THIRD -> btn3.isChecked = true
        }
    }

    fun setRadioBtnsChangeListener(callback: ( group: RadioGroupConstraintLayout, checkedId: Int) -> Unit) {
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            callback(group, checkedId)
        }
    }

    fun checkMe(id: Int) {
        when (id) {
            R.id.radioButton1 -> btn1.isChecked = true
            R.id.radioButton2 -> btn2.isChecked = true
            R.id.radioButton3 -> btn3.isChecked = true
        }
    }
}