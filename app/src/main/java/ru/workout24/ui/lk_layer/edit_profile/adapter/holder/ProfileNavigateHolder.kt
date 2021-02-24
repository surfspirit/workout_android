package ru.workout24.ui.lk_layer.edit_profile.adapter.holder

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import ru.workout24.R
import ru.workout24.ui.lk_layer.edit_profile.adapter.ProfileItemsListener
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileArrowViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import org.jetbrains.anko.alignParentEnd
import org.jetbrains.anko.find

class ProfileNavigateHolder(val view: View, val listener: ProfileItemsListener) : AbstractTypeHolder<ProfileArrowViewModel>(view) {
    private val imageView by lazy {
        val context = view.context
        val icon = ImageView(context)
        val height = context.resources.getDimensionPixelSize(R.dimen.profile_arrow_height)
        val params = RelativeLayout.LayoutParams(
            height,
            height
        )
        params.marginEnd = context.resources.getDimensionPixelSize(R.dimen.profile_margin_end)
        params.topMargin = context.resources.getDimensionPixelSize(R.dimen.profile_margin_top)
        params.alignParentEnd()
        icon.layoutParams = params
        icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.back_alt))
        icon.rotation = 180f
        icon.imageTintList = ColorStateList.valueOf(Color.parseColor("#FF8E8E93"))
        icon
    }

    init {
        view as ViewGroup
        view.addView(imageView)
    }

    override fun bind(data: ProfileArrowViewModel) {
        val title = view.find<TextView>(R.id.title)
        title.text = data.title
        if (data.canNavigate) {
            view.setOnClickListener { listener.onNavigateClick(data) }
        }
    }
}