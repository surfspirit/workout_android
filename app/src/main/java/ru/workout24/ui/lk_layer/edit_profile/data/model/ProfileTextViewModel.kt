package ru.workout24.ui.lk_layer.edit_profile.data.model

import ru.workout24.utills.ITEM_TYPE
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

open class ProfileTextViewModel(
    private val modelType: ITEM_TYPE = ITEM_TYPE.TEXT,
    val title: String,
    var value: CharSequence?,
    val hint: String? = null,
    val id: String? = null,
    val placeHolder: String? = null
//    val groupId: String? = null
) : AbstractTypeViewModel(modelType.value)