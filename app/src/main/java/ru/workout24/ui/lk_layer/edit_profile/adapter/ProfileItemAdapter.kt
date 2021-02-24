package ru.workout24.ui.lk_layer.edit_profile.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.workout24.R
import ru.workout24.ui.lk_layer.edit_profile.adapter.holder.ProfileNavigateHolder
import ru.workout24.ui.lk_layer.edit_profile.adapter.holder.ProfileSwitchHolder
import ru.workout24.ui.lk_layer.edit_profile.adapter.holder.ProfileTextHolder
import ru.workout24.ui.lk_layer.edit_profile.adapter.holder.ProfileWhiteSpaceHolder
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileArrowViewModel
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileSwitchViewModel
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileTextViewModel
import ru.workout24.utills.ITEM_TYPE
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeHolder
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeAdapter
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import org.jetbrains.anko.backgroundColor
import ru.workout24.utills.SwitchButton

class ProfileItemAdapter(private val listener: ProfileItemsListener) :
    AbstractTypeAdapter() {
    override fun createHolder(
        inflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): AbstractTypeHolder<out AbstractTypeViewModel> {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_profile, parent, false)
        return when (viewType) {
            ITEM_TYPE.ARROW.value -> ProfileNavigateHolder(view, listener)
            ITEM_TYPE.SWITCH.value -> ProfileSwitchHolder(view, listener)
            ITEM_TYPE.DATE.value, ITEM_TYPE.NUMBER.value, ITEM_TYPE.SELECT.value, ITEM_TYPE.TEXT.value -> ProfileTextHolder(
                view,
                listener
            )
            // TODO: переделать на декораторы
            ITEM_TYPE.WHITE_SPACER.value -> ProfileWhiteSpaceHolder(createWhiteSpaceView(parent.context))
            else -> throw IllegalArgumentException("Нет такого типа")
        }
    }

    private fun createWhiteSpaceView(context: Context): View {
        val view = View(context)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            context.resources.getDimensionPixelSize(R.dimen.profile_white_space_height)
        )
        view.layoutParams = params
        view.backgroundColor = Color.TRANSPARENT
        return view
    }
}

interface ProfileItemsListener {
    fun onItemClick(data: ProfileTextViewModel, position: Int)
    fun onSwitchChange(switch: SwitchButton, data: ProfileSwitchViewModel)
    fun onNavigateClick(data: ProfileArrowViewModel)
}