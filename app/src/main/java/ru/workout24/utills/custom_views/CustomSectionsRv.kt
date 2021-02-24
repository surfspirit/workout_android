package ru.workout24.utills.custom_views

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ru.workout24.R
import ru.workout24.utills.base.BaseAdapter

class CustomSectionsRv : ConstraintLayout {

    var adapter: BaseAdapter? = null
        set(value) {
            rv.adapter = value
            field = value
            adapter?.setOnDataUpdated {
                adapter?.data?.size?.let {
                    onDataUpdated(it > 0)
                }
            }
        }

    private val progress: ProgressBar by lazy {
        findViewById<ProgressBar>(R.id.progress)
    }

    private val header: ConstraintLayout by lazy {
        findViewById<ConstraintLayout>(R.id.rv_header)
    }

    private val rv: RecyclerView by lazy {
        findViewById<RecyclerView>(R.id.rv_list)
    }

    private val footer: ConstraintLayout by lazy {
        findViewById<ConstraintLayout>(R.id.rv_footer)
    }

    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr) {
        initView(context, attrs, defStyleAttr)
    }

    private fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        /*
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomButton)
        val text = typedArray.getString(R.styleable.CustomButton_textBtn)
        val isRounded = typedArray.getBoolean(R.styleable.CustomButton_isRounded, false)

        typedArray.recycle()
        */
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_section_rv, this, true)
    }

    private fun onDataUpdated(showViews: Boolean) {
        if (showViews) {
            header.visibility = View.VISIBLE
            rv.visibility = View.VISIBLE
            footer.visibility = View.VISIBLE
        } else {
            header.visibility = View.INVISIBLE
            rv.visibility = View.INVISIBLE
            footer.visibility = View.INVISIBLE
        }

    }

    fun showProgress(value: Boolean = true) {
        if (value) {
            progress.visibility = View.VISIBLE
        } else {
            progress.visibility = View.INVISIBLE
        }
    }

}