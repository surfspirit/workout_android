package ru.workout24.utills.custom_views

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import ru.workout24.R
import ru.workout24.utills.hide
import ru.workout24.utills.show
import com.facebook.FacebookSdk

class CustomAwesomeVimeoPlayer : ConstraintLayout {

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {

        init(context, attrs, defStyleAttr)
    }
    private val wv_player: WebView by lazy {
        findViewById<WebView>(R.id.wv_player)
    }

    private val wv_progress: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.wv_progress)
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
    fun initVimeoPlayer(video_url:String) {
        wv_player.settings.javaScriptEnabled = true
        wv_player.webViewClient = Browser_home()
        wv_player.webChromeClient = MyChrome()
        wv_player.settings.javaScriptEnabled = true
        wv_player.settings.allowFileAccess = true
        wv_player.settings.setAppCacheEnabled(true)
        wv_progress.show()
        val data = "<html><head><style type=\"text/css\">body {background-color: black;color: black;}</style><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0\"></head><body style=\"margin:0\">\n" +
                "<iframe id=\"f_iframe\" src=\""+video_url+"\" frameborder=\"0\" webkitallowfullscreen=\"\" mozallowfullscreen=\"\" allowfullscreen=\"\" style=\"\n" +
                "    width: 100vw;\n" +
                "    height: 100vh;\n" +
                "\"></iframe></body></html>"
        wv_player.loadDataWithBaseURL("", data, "text/html", "UTF-8", "")
    }


    internal inner class Browser_home : WebViewClient() {

        override fun onPageFinished(view: WebView?, url: String?) {
            wv_progress.hide()
            super.onPageFinished(view, url)

        }
    }


    private inner class MyChrome internal constructor() : WebChromeClient() {

        private var mCustomView: View? = null
        private var mCustomViewCallback: CustomViewCallback? = null
        protected var mFullscreenContainer: FrameLayout? = null
        private var mOriginalOrientation: Int = 0
        private var mOriginalSystemUiVisibility: Int = 0

        override fun getDefaultVideoPoster(): Bitmap? {
            return if (mCustomView == null) {
                null
            } else BitmapFactory.decodeResource(FacebookSdk.getApplicationContext().resources, 2130837573)
        }

        override fun onHideCustomView() {
            ((context as Activity).window?.decorView as FrameLayout).removeView(this.mCustomView)
            this.mCustomView = null
            (context as Activity).window?.decorView?.systemUiVisibility = this.mOriginalSystemUiVisibility
            (context as Activity).requestedOrientation = this.mOriginalOrientation
            this.mCustomViewCallback!!.onCustomViewHidden()
            this.mCustomViewCallback = null
        }

        override fun onShowCustomView(paramView: View, paramCustomViewCallback: CustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView()
                return
            }
            this.mCustomView = paramView
            this.mOriginalSystemUiVisibility = (context as Activity).window.decorView.systemUiVisibility
            this.mOriginalOrientation = (context as Activity).requestedOrientation
            this.mCustomViewCallback = paramCustomViewCallback
            ((context as Activity).window.decorView as FrameLayout).addView(this.mCustomView, FrameLayout.LayoutParams(-1, -1))
            (context as Activity).window.decorView.systemUiVisibility = 3846
        }
    }

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.awesome_player_layout, this, true)
    }
}