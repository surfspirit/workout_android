package ru.workout24.ui.lk_layer.edit_profile.data.model

import ru.workout24.utills.ITEM_TYPE

class ProfileRangeViewModel(
    title: String,
    value: Float?,
    val min: Float = 0F,
    val max: Float = Float.MAX_VALUE,
    val suffix: String,
    hint: String,
    id: String? = null,
//    groupId: String? = null,
    placeHolder: String?  = null,
    var isLocked: Boolean = false
) : ProfileTextViewModel(ITEM_TYPE.NUMBER, title, value?.toString(), hint, id, placeHolder/*, groupId*/)