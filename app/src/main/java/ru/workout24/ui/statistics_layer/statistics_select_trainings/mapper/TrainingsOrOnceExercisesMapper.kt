package ru.workout24.ui.statistics_layer.statistics_select_trainings.mapper

import ru.workout24.ui.statistics_layer.statistics_select_trainings.TrainingsOrOnceExercisesFilterFragment
import ru.workout24.ui.trainings.pager.adapters.MultiCheckCategory
import androidx.arch.core.util.Function
import ru.workout24.network.InventoryResponse
import ru.workout24.network.MuscleGroupResponse
import ru.workout24.ui.statistics_layer.statistics_select_trainings.VMTrainingsOrOnceExercisesFilter
import ru.workout24.ui.trainings.once_exercise.Inventories
import ru.workout24.ui.trainings.once_exercise.MuscleGroup
import ru.workout24.ui.trainings.pager.pojos.Criteria

class TrainingsOrOnceExercisesMapper(private val type: TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE) :
    Function<Pair<InventoryResponse?, MuscleGroupResponse?>, MutableList<MultiCheckCategory>> {

    /**
     * Принимаем данные с сервера на маппинг
     */
    override fun apply(data: Pair<InventoryResponse?, MuscleGroupResponse?>?): MutableList<MultiCheckCategory> {
        return data?.let {
            // приходят все фильтры в одной куче, отфильтровываем их для каждого экрана
            return@let when (type) {

                TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE.TRAININGS -> mutableListOf(
                    MultiCheckCategory(
                        title = "Группы мышц",
                        serverName = CategoryApiNames.MUSCLE_GROUP_IDS.value,
                        items = mapMuscleGroup(it.second?.muscle_groups)
                    ),
                    MultiCheckCategory(
                        title = "Оборудование",
                        serverName = CategoryApiNames.INVENTORY_IDS.value,
                        items = mapInventories(it.first?.inventories)
                    ),
                    MultiCheckCategory(
                        title = "Продолжительность тренировки",
                        serverName = CategoryApiNames.ESTIMATE_DURATION.value,
                        items = estimateTimeTraining
                    )
                )

                TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE.ONCE_EXERCISES -> mutableListOf(
                    MultiCheckCategory(
                        title = "Группы мышц",
                        serverName = CategoryApiNames.MUSCLE_GROUP_IDS.value,
                        items = mapMuscleGroup(it.second?.muscle_groups)
                    ),
                    MultiCheckCategory(
                        title = "Оборудование",
                        serverName = CategoryApiNames.INVENTORY_IDS.value,
                        items = mapInventories(it.first?.inventories)
                    ),
                    MultiCheckCategory(
                        title = "Продолжительность упражнения",
                        serverName = CategoryApiNames.ESTIMATE_TIME.value,
                        items = estimateTimeExrcise
                    )
                )

                TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE.TRAINING_SET -> mutableListOf(
                    MultiCheckCategory(
                        title = "Цели тренировки",
                        serverName = CategoryApiNames.GOALS.value,
                        items = goalsFilters
                    ),
                    MultiCheckCategory(
                        title = "Группы мышц",
                        serverName = CategoryApiNames.MUSCLE_GROUP_IDS.value,
                        items = mapMuscleGroup(it.second?.muscle_groups)
                    ),
                    MultiCheckCategory(
                        title = "Оборудование",
                        serverName = CategoryApiNames.INVENTORY_IDS.value,
                        items = mapInventories(it.first?.inventories)
                    ),
                    MultiCheckCategory(
                        title = "Продолжительность программы",
                        serverName = CategoryApiNames.WEEK_COUNT.value,
                        items = estimateTimeTrainingSetFilters
                    )
                )

            }

        } ?: mutableListOf()
    }

    private fun mapInventories(inventories: List<Inventories>?): List<Criteria> =
        inventories?.map { inventory -> Criteria(name = inventory.name, value = inventory.id) } ?: listOf()

    private fun mapMuscleGroup(muscleGroups:List<MuscleGroup>?): List<Criteria> =
        muscleGroups?.map { group -> Criteria(name = group.name, value = group.id) } ?: listOf()
}

private const val conditionKey = "condition"
private const val dataKey = "data"

enum class CategoryApiNames(val value: String) {
    GOALS("goals"),
    WEEK_COUNT("weeks_count"),
    ESTIMATE_TIME("estimate_time"),
    ESTIMATE_DURATION("estimate_duration"),
    MUSCLE_GROUP_IDS("muscle_group_ids"),
    INVENTORY_IDS("inventory_ids")
}

private val estimateTimeExrcise = listOf(
    Criteria(name = "< 1 минуты", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "LT", data = 60)),
    Criteria(name = "1-5 минут", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "RANGE", data = arrayOf(60,300))),
    Criteria(name = "5-15 минут", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "RANGE", data = arrayOf(300,900))),
    Criteria(name = "> 15 минут", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "GT", data = 900))
)

private val estimateTimeTraining = listOf(
    Criteria(name = "< 15 минут", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "LT", data = 900)),
    Criteria(name = "15 - 30 минут", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "RANGE", data = arrayOf(900,1800))),
    Criteria(name = "30 - 45 минут", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "RANGE", data = arrayOf(1800,2700))),
    Criteria(name = "> 45 минут", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "GT", data = 2700))
)

private val estimateTimeTrainingSetFilters = listOf(
    Criteria(name = "< 2 недель", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "LT", data = 2)),
    Criteria(name = "2 - 4  недели", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "RANGE", data = arrayOf(2,4))),
    Criteria(name = "4 - 6 недель", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "RANGE", data = arrayOf(4,6))),
    Criteria(name = "> 6 недель", value = VMTrainingsOrOnceExercisesFilter.FilterQuery.AnyFilterQuery(condition = "GT", data = 6))
)

private val goalsFilters = listOf(
    Criteria(name = "Мышечная масса", value = "MUSCLE_BUILDING"),
    Criteria(name = "Снизить вес", value = "LOSE_WEIGHT"),
    Criteria(name = "Здоровье и тонус", value = "HEALTHY_AND_STRONG")
)

//enum class Type(val value: String) {
//    NONE("NONE"),
//    STRING("STRING"),
//    NUMBER("NUMBER"),
//    DATE("DATE"),
//    DATE_LIST("DATE_LIST"),
//    STRING_LIST("STRING_LIST"),
//    INT_LIST("INT_LIST")
//}
//
//enum class Condition(val value: String) {
//    NONE("NONE"), LT("LT"), GT("GT"), EQ("EQ"),
//    RANGE("RANGE"), CONTAIN("CONTAIN"), ANY("ANY"),
//    INVENTORY_EQ("INVENTORY_EQ"), INVENTORY_ANY("INVENTORY_ANY"),
//    MUSCLE_GROUP_EQ("MUSCLE_GROUP_EQ"), MUSCLE_GROUP_ANY("MUSCLE_GROUP_ANY")
//}
//
//enum class Target(val value: String) {
//    NONE("NONE"), EXERCISES("EXERCISES"),
//    TRAINING("TRAINING"), TRAINING_SET("TRAINING_SET"),
//    SINGLE_EXERCISES("SINGLE_EXERCISES")
//}