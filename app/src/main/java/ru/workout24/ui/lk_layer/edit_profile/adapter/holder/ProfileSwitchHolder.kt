package ru.workout24.ui.lk_layer.edit_profile.adapter.holder

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import ru.workout24.R
import ru.workout24.ui.lk_layer.edit_profile.adapter.ProfileItemsListener
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileSwitchViewModel
import ru.workout24.utills.SwitchButton
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import org.jetbrains.anko.alignParentEnd
import org.jetbrains.anko.find

class ProfileSwitchHolder(val view: View, val listener: ProfileItemsListener) : AbstractTypeHolder<ProfileSwitchViewModel>(view) {
    private val switchView by lazy {
        val context = view.context
        val switch = SwitchButton(context)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginEnd = context.resources.getDimensionPixelSize(R.dimen.profile_margin_end)
        params.topMargin = context.resources.getDimensionPixelSize(R.dimen.profile_switch_margin_top)
        params.alignParentEnd()
        switch.layoutParams = params
        switch
    }

    init {
        view as ViewGroup
        view.addView(switchView)
    }

    override fun bind(data: ProfileSwitchViewModel) {
        val title = view.find<TextView>(R.id.title)
        title.text = data.title
        switchView.isChecked = data.isChecked
        if (!data.isLocked) {
            switchView.setOnCheckedChangeListener { view, isChecked ->
                data.isChecked = isChecked
                listener.onSwitchChange(view, data)
            }
        }
    }
}