package ru.workout24.ui.statistics_layer.edit_statistics_entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.workout24.network.Api
import ru.workout24.network.UserStatistic
import ru.workout24.room.AppDatabase
import ru.workout24.ui.statistics_layer.statistics.StatisticsDateFragment
import javax.inject.Singleton

/**
 * [ViewModelProvider.Factory] для передачи type и statisticType в конструктор вьюмодели
 * @param type  тип открытия фрагмента(ADD -- добавление статистики, EDIT -- редактирование)
 */
@Suppress("UNCHECKED_CAST")
@Singleton
class VMEditStatisticsEntryFactory(
    private val api: Api,
    private val db: AppDatabase,
    private val data: UserStatistic?,
    private val type: FRAGMENT_OPEN_TYPE,
    private val statisticType: StatisticsDateFragment.DateItemType
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        VMEditStatisticsEntry(api, db, type, statisticType, data) as T
}