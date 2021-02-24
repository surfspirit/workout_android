package ru.workout24.ui.lk_layer.edit_profile.adapter.holder

import android.graphics.Color
import android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import ru.workout24.R
import ru.workout24.ui.lk_layer.edit_profile.adapter.ProfileItemsListener
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileRangeViewModel
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileTextViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import org.jetbrains.anko.alignParentEnd
import org.jetbrains.anko.find

class ProfileTextHolder(val view: View, val listener: ProfileItemsListener) : AbstractTypeHolder<ProfileTextViewModel>(view) {
    private val textView by lazy {
        val context = view.context
        val textView = TextView(context)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginEnd = context.resources.getDimensionPixelSize(R.dimen.profile_margin_end)
        params.topMargin = context.resources.getDimensionPixelSize(R.dimen.profile_margin_top)
        params.alignParentEnd()
        textView.layoutParams = params
        textView.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
        textView.setTextColor(Color.BLACK)
        textView
    }

    init {
        view as ViewGroup
        view.addView(textView)
    }

    override fun bind(data: ProfileTextViewModel) {
        if (data is ProfileRangeViewModel) {
            bindRange(data)
            return
        }
        val title = view.find<TextView>(R.id.title)
        title.text = data.title
        if (data.id == "name" || data.id == "surname") textView.inputType = TYPE_TEXT_FLAG_CAP_SENTENCES
        textView.text = data.value
        view.setOnClickListener { listener.onItemClick(data, adapterPosition) }
    }

    private fun bindRange(data: ProfileRangeViewModel) {
        val title = view.find<TextView>(R.id.title)
        title.setText(data.title)

        if (data.value == null) {
            textView.setText(data.placeHolder)
        } else {
            textView.setText("${data.value} ${data.suffix}")
        }
        if(!data.isLocked) {
            view.setOnClickListener { listener.onItemClick(data, adapterPosition) }
        }
    }
}