package ru.workout24.utills.custom_views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.text.Html
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import ru.workout24.R
import kotlinx.android.synthetic.main.custom_toolbar_sub.view.*

class CustomToolbarSub: ConstraintLayout {


    private val title: TextView by lazy {
        findViewById<TextView>(R.id.txt_title)
    }

    private val backBtn: ImageView by lazy {
        findViewById<ImageView>(R.id.iv_back)
    }

    private val pic1Btn: ImageView by lazy {
        findViewById<ImageView>(R.id.iv_pic1)
    }

    private val sub_header: TextView by lazy {
        findViewById<TextView>(R.id.txt_sub_header)
    }

    private val subBtn: Button by lazy {
        findViewById<Button>(R.id.sub_button)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0)
            : super(context, attrs, defStyleAttr)
    {
        initToolbar(attrs)
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int) : super(context, attrs, defStyleAttr)
    {
        initToolbar(attrs)
    }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.custom_toolbar_sub, this, true)
    }

    private fun initToolbar(attrs: AttributeSet?) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomToolbar)
        val title = typedArray.getString(R.styleable.CustomToolbar_title)
        val backImg = typedArray.getDrawable(R.styleable.CustomToolbar_backSrc)
        val pic1Img = typedArray.getDrawable(R.styleable.CustomToolbar_pic1Src)

        this.title.text = title ?: ""

        backImg?.let {
            backBtn.setImageDrawable(it)
        }

        pic1Img?.let {
            pic1Btn.setImageDrawable(it)
        }

        this.txt_sub_header.text = Html.fromHtml(resources.getString(R.string.sub_toolbar_header))

        typedArray.recycle()

    }

    fun setOnBackClick(onClick: () -> Unit) {
        backBtn.setOnClickListener { onClick() }
    }

    fun setOnPic1Click(onClick: () -> Unit) {
        pic1Btn.setOnClickListener { onClick() }
    }

    fun setBackDrawable(resId: Int){
        backBtn.setImageResource(resId)
    }

    fun setTitleText(text: String){
        title.text = text
    }

    fun setOnSubClick(onClick: () -> Unit) {
        subBtn.setOnClickListener { onClick() }
    }

}