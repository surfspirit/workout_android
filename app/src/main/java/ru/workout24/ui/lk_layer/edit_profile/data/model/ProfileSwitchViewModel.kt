package ru.workout24.ui.lk_layer.edit_profile.data.model

import ru.workout24.utills.ITEM_TYPE
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

class ProfileSwitchViewModel(val title: String, var isChecked: Boolean, val id: String? = null, val isLocked: Boolean = false) :
    AbstractTypeViewModel(ITEM_TYPE.SWITCH.value)