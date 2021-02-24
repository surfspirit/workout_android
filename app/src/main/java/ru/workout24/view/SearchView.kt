package ru.workout24.view


import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.constraintlayout.widget.ConstraintLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import ru.workout24.R
import kotlinx.android.synthetic.main.custom_search_view.view.*


class CustomSearchView : ConstraintLayout {

    private val searchInput: EditText by lazy {
        findViewById<EditText>(R.id.et_search_input)
    }

    private val searchBtn: ImageView by lazy {
        findViewById<ImageView>(R.id.iv_search_btn)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    )
            : super(context, attrs, defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr)

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.custom_search_view, this, true)


    /*    val bg = GradientDrawable()
        bg.setColor(Color.WHITE)
        bg.cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25f, context.resources.displayMetrics)
        bg.setStroke(1, context.getColor(R.color.darkGray))


        backgroundDrawable = bg*/
    }

    fun getText(): String {
        return et_search_input.text.toString()
    }

    fun setText(s: String) {
        et_search_input.setText(s)
    }

    fun onTextChanged(listener: (s: CharSequence?) -> Unit) {
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listener(s)
            }

        })
    }
}