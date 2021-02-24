package ru.workout24.ui.wg_bonuses.data.viewmodel

import ru.workout24.utills.base.typed_holder_adapter.AbstractIdTypeViewModel

const val LEADER_TYPE = 14

data class LeaderViewModel(
    val itemId: String,
    val name: String,
    val avatarUrl: String?,
    val wgBonuses: Int,
    val position: Int
): AbstractIdTypeViewModel(LEADER_TYPE, itemId)