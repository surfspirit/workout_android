package ru.workout24.ui.wg_bonuses.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.workout24.R
import com.mikhaellopez.circularimageview.CircularImageView
import org.jetbrains.anko.find

class LeaderView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null) :
    RelativeLayout(context, attr) {
    private val avatar by lazy {
        find<CircularImageView>(R.id.avatar)
    }

    private val name by lazy {
        find<TextView>(R.id.name)
    }

    private val bonuses by lazy {
        find<TextView>(R.id.bonuses)
    }

    private val position by lazy {
        find<TextView>(R.id.position)
    }

    init {
        View.inflate(context, R.layout.item_leader, this)
    }

    fun setData() {

    }

    fun toggleItemSelect(value: Boolean) {
        val nameColor = if (value) {
            ContextCompat.getColor(context, R.color.lightGray)
        } else {
            ContextCompat.getColor(context, R.color.warmGray)
        }
        val bonusesColor = if (value) {
            ContextCompat.getColor(context, R.color.lightGray)
        } else {
            ContextCompat.getColor(context, R.color.black)
        }
        val background = if (value) {
            ContextCompat.getColor(context, R.color.selectGray)
        } else {
            Color.WHITE
        }
        name.setTextColor(nameColor)
        bonuses.setTextColor(bonusesColor)
        setBackgroundColor(background)
    }
}