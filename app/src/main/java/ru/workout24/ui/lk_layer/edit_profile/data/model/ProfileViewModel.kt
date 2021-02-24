package ru.workout24.ui.lk_layer.edit_profile.data.model

import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

data class ProfileViewModel(
    val profileAvatar: String,
    val profileItems: ArrayList<AbstractTypeViewModel>
)