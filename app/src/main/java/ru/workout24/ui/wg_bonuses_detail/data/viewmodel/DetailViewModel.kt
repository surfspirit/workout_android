package ru.workout24.ui.wg_bonuses_detail.data.viewmodel

import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

data class DetailViewModel(
    val detailType: Int,
    val title: String,
    val value: String
) : AbstractTypeViewModel(detailType)