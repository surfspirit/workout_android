package ru.workout24.ui.lk_layer.edit_profile.data.model

import ru.workout24.utills.ITEM_TYPE
import ru.workout24.utills.getKeyByValue

class ProfileSelectEnumViewModel<ENUM_TYPE>(
    title: String,
    value: ENUM_TYPE,
    val keyMap: Map<ENUM_TYPE, String>,
    val variants: List<ENUM_TYPE>,
    hint: String,
    id: String? = null,
    val isLocked: Boolean = false
) : ProfileTextViewModel(ITEM_TYPE.SELECT, title, keyMap[value] ?: "", id = id) {
    fun getEnumType(): ENUM_TYPE = keyMap.getKeyByValue(value)!!
}