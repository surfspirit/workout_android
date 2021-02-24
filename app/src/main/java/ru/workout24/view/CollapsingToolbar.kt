package ru.workout24.view

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import ru.workout24.R
import ru.workout24.ui.RootActivity
import ru.workout24.utills.hide
import kotlinx.android.synthetic.main.activity_main.*


class CollapsingToolbar : Toolbar {

    private val title: TextView by lazy {
        findViewById<TextView>(R.id.txt_title)
    }

    private val backBtn: ImageView by lazy {
        findViewById<ImageView>(R.id.iv_back)
    }

    private val pic1Btn: ImageView by lazy {
        findViewById<ImageView>(R.id.iv_pic1)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    )
            : super(context, attrs, defStyleAttr) {
        initToolbar(attrs)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr) {
        initToolbar(attrs)
    }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.custom_toolbar, this, true)
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

        backBtn.setOnClickListener { onBackClickListener() }

        pic1Img?.let {
            pic1Btn.setImageDrawable(it)
        }

        typedArray.recycle()

    }

    private var onClickListener = {
        if (context is RootActivity) {
            val con = context as RootActivity
            if (con.supportFragmentManager.backStackEntryCount > 0)
                con.controller?.popBackStack()
        }
    }

    fun onBackClickListener() {
        if (context is RootActivity)
            (context as RootActivity).progress.hide()
        onClickListener()
    }

    fun setOnBackClick(onClick: () -> Unit) {
        onClickListener = onClick
    }

    fun setOnPic1Click(onClick: () -> Unit) {
        pic1Btn.setOnClickListener { onClick() }
    }

    fun setBackDrawable(resId: Int) {
        backBtn.setImageResource(resId)
    }

    fun setPic1Drawable(resId: Int) {
        pic1Btn.setImageResource(resId)
    }

    fun hideBackButton() {
        backBtn.hide()
    }

    fun setTitleText(text: String) {
        title.text = text
    }

}