package ru.workout24.ui.wg_bonuses_detail.data.viewmodel

import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

data class DetailHeaderViewModel(
    val title: String,
    val headerType: Int
): AbstractTypeViewModel(headerType)