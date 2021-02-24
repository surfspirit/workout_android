package ru.workout24.ui.lk_layer.edit_profile.data.model

import androidx.annotation.IdRes
import ru.workout24.utills.ITEM_TYPE
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

class ProfileArrowViewModel(
    var title: String,
    @IdRes val navigateTo: Int,
    val canNavigate: Boolean
) : AbstractTypeViewModel(ITEM_TYPE.ARROW.value)