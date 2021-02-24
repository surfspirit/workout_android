package ru.workout24.ui.statistics_layer.statistics_select_trainings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.workout24.network.*
import ru.workout24.room.AppDatabase
import ru.workout24.ui.statistics_layer.statistics_select_trainings.mapper.TrainingsOrOnceExercisesMapper
import ru.workout24.utills.base.BaseViewModel
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import ru.workout24.ui.statistics_layer.once_exercises_trainings.CombinedTwoLiveData
import ru.workout24.ui.statistics_layer.statistics_select_trainings.mapper.CategoryApiNames
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * ViewModel для [TrainingsOrOnceExercisesFilterFragment]
 * @param fragmentType  тип открываемого фрагмента
 */
class VMTrainingsOrOnceExercisesFilter(
    private val api: Api,
    private val db: AppDatabase,
    private val resourceProvider: ResourceProvider,
    private val fragmentType: TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE
) : BaseViewModel() {
    private val inventoryResource = resourceProvider.inventoryResource
    private val muscleGroupResource = resourceProvider.muscleGroupsResource
    val errors = inventoryResource.errorLiveData
    private val adapterData = Transformations.map(
        CombinedTwoLiveData(
            inventoryResource.liveData,
            muscleGroupResource.liveData
        ) { inventory, muscle -> Pair(inventory, muscle) },
        TrainingsOrOnceExercisesMapper(fragmentType)
    )
    private val isAcceptFilterEnable: MutableLiveData<Boolean> = MutableLiveData()
    private val selectedFilterItems = HashMap<String, Any>(0)

    init {
        inventoryResource.load()
        muscleGroupResource.load()
    }

    private fun checkSelectedFilterItemsEmpty() {
        isAcceptFilterEnable.postValue(selectedFilterItems.size != 0)
    }

    fun getFilterItems(): HashMap<String, String> {
        return HashMap(selectedFilterItems.mapValues {
            it.value.toString()
        })
    }

    /**
     * Добавляем фильтр
     * @param group имя группы для запроса
     * @param name имя фильтра в группе
     */
    fun setFilterItem(group: String, value: Any?) {
        when (group) {
            CategoryApiNames.GOALS.value, CategoryApiNames.MUSCLE_GROUP_IDS.value, CategoryApiNames.INVENTORY_IDS.value -> {
                if (!selectedFilterItems.containsKey(group)) {
                    selectedFilterItems[group] =
                        FilterQuery.ArrayFilterQuery(condition = "ANY", data = arrayListOf())
                }
                value?.let {
                    (selectedFilterItems[group] as FilterQuery.ArrayFilterQuery).data.add(
                        it
                    )
                }
            }
            CategoryApiNames.ESTIMATE_TIME.value, CategoryApiNames.ESTIMATE_DURATION.value, CategoryApiNames.WEEK_COUNT.value -> {
                if (!selectedFilterItems.containsKey(group)) {
                    selectedFilterItems[group] = arrayListOf<FilterQuery.AnyFilterQuery>()
                }
                value?.let {
                    (selectedFilterItems[group] as ArrayList<FilterQuery.AnyFilterQuery>).add(
                        it as FilterQuery.AnyFilterQuery
                    )
                }
            }
        }
        checkSelectedFilterItemsEmpty()
    }

    /**
     * Удаляем фильтр
     * @param group имя группы для запроса
     * @param name имя фильтра в группе
     */
    fun deleteFilterItem(group: String, value: Any?) {
        when (group) {
            CategoryApiNames.GOALS.value, CategoryApiNames.MUSCLE_GROUP_IDS.value, CategoryApiNames.INVENTORY_IDS.value -> {
                value?.let {
                    val filterItem = selectedFilterItems[group] as FilterQuery.ArrayFilterQuery
                    filterItem.data.remove(it)
                    if (filterItem.data.isEmpty()) {
                        selectedFilterItems.remove(group)
                    }
                }
            }
            CategoryApiNames.ESTIMATE_TIME.value, CategoryApiNames.ESTIMATE_DURATION.value, CategoryApiNames.WEEK_COUNT.value -> {
                (value as? FilterQuery.AnyFilterQuery)?.let {
                    val filterItem =
                        selectedFilterItems[group] as ArrayList<FilterQuery.AnyFilterQuery>
                    filterItem.remove(it)
                    if (filterItem.isEmpty()) {
                        selectedFilterItems.remove(group)
                    }
                }
            }
        }
        checkSelectedFilterItemsEmpty()
    }

    fun resetFilterItems() {
        selectedFilterItems.clear()
        checkSelectedFilterItemsEmpty()
    }

    /**
     * Получение observable данных для адаптера
     */
    fun getAdapterData() = adapterData

    /**
     * Получение observable флага для enable/disable кнопки "Применить фильтр"
     * и visible/invisible для кнопки "Сбросить фильтр"
     */
    fun getIsAcceptFilterEnable() = isAcceptFilterEnable

    sealed class FilterQuery {
        class ArrayFilterQuery(
            @SerializedName("condition") val condition: String,
            @SerializedName("data") val data: ArrayList<Any>
        ) : FilterQuery()

        class AnyFilterQuery(
            @SerializedName("condition") val condition: String,
            @SerializedName("data") val data: Any
        ) : FilterQuery()

        override fun toString(): String {
            return Gson().toJson(this)
        }
    }

}

/**
 * Factory [VMTrainingsOrOnceExercisesFilter] для передачи типа фрагмента в конструктор
 * @param type тип фрагмента
 */
class VMTrainingsOrOnceExercisesFilterFactory(
    val api: Api,
    val db: AppDatabase,
    val resourceProvider: ResourceProvider,
    val type: TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return VMTrainingsOrOnceExercisesFilter(api, db, resourceProvider, type) as T
    }

}