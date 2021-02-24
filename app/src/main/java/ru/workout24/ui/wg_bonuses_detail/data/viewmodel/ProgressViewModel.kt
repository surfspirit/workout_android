package ru.workout24.ui.wg_bonuses_detail.data.viewmodel

import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel

data class ProgressViewModel(
    val progressType: Int,
    val bonusesCount: Int,
    val totalLevelBonuses: Int,
    val bonusesLeft: Int
) : AbstractTypeViewModel(progressType)