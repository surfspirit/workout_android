package ru.workout24.ui.statistics_layer.edit_statistics_entry

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import ru.workout24.network.*
import ru.workout24.room.AppDatabase
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileArrowViewModel
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileDateViewModel
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileRangeViewModel
import ru.workout24.ui.lk_layer.edit_profile.data.model.ProfileWhiteSpaceViewModel
import ru.workout24.ui.statistics_layer.edit_statistics_entry.data.model.PhotosViewModel
import ru.workout24.ui.statistics_layer.statistics.StatisticsDateFragment
import ru.workout24.utills.*
import ru.workout24.utills.base.BaseViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class VMEditStatisticsEntry(
    private val api: Api,
    private val db: AppDatabase,
    private val type: FRAGMENT_OPEN_TYPE,
    private val statisticType: StatisticsDateFragment.DateItemType,
    private val data: UserStatistic?
) : BaseViewModel(), ImagePicker.OnUploadImageListener, TargetListener {
    // измененные поля для отправки на сервер
    private val fieldsMap by lazy {
        hashMapOf<String, Any>()
    }

    // список вьюмоделей для отображения
    // определенных view holder-ов в recycler view
    private val adapterData by lazy {
        arrayListOf<AbstractTypeViewModel>()
    }

    // список ссылок на картинки пользователя(их не более трех)
    private val urls = arrayListOf<String>()

    // сигнализирует что картинка пользователя загружена
    // на сревер и что адаптеру с картинками нужно обновиться
    val imagesUrl = MutableLiveData<String>()
    val statisticsItems = MutableLiveData<ArrayList<AbstractTypeViewModel>>()
    val popBackStackIsSuccess = RxSingleLiveData<String>()
    val errorLiveData = RxErrorConsumer()

    init {
        TrainingTarget.setTargetListener(this)
    }

    fun getStatisticsEntryItems() {
        val canDateModify = type == FRAGMENT_OPEN_TYPE.ADD
        val dateWithTime = statisticType == StatisticsDateFragment.DateItemType.ITEM_MANUAL
        if (adapterData.isEmpty()) {
            data?.photosUrl?.let {
                // TODO: почему то из базы приходит singletonList с одной пустой строкой из базы
                //  пока добавил костыль в виде фильтра
                urls.addAll(it.filter { it.isNotEmpty() }.asIterable())
            }
            // создаем список отображаемых вьюх вадаптере на основе вьюмоделей
            adapterData.addAll(
                arrayOf(
                    ProfileDateViewModel(
                        "Дата",
                        createDate(data?.createdAt),
                        "created_at",
                        canDateModify,
                        dateWithTime
                    ),
                    ProfileArrowViewModel(
                        data?.target?.name ?: "Программа тренировок или упражнения",
                        statisticType.navigateId,
                        data == null
                    ),
                    PhotosViewModel(urls, id = "photos_url"),
                    ProfileWhiteSpaceViewModel(),
                    ProfileRangeViewModel(
                        title = "Вес",
                        value = data?.weight ?: 60F,
                        suffix = "кг",
                        hint = "Введите вес",
                        min = 60F,
                        max = 300F,
                        id = "weight"
                    ),
                    ProfileWhiteSpaceViewModel(),
                    ProfileRangeViewModel(
                        title = "Процент жира",
                        value = data?.fatPercentage,
                        suffix = "%",
                        hint = "Введите процент жира",
                        id = "fat_percentage",
                        placeHolder = "Опционально"
                    ),
                    ProfileRangeViewModel(
                        title = "Шея",
                        value = data?.neckSize,
                        suffix = "см",
                        hint = "Введите значение",
                        id = "neck_size",
                        placeHolder = "Опционально"
                    ),
                    ProfileRangeViewModel(
                        title = "Грудь",
                        value = data?.chestSize,
                        suffix = "см",
                        hint = "Введите значение",
                        id = "chest_size",
                        placeHolder = "Опционально"
                    ),
                    ProfileRangeViewModel(
                        title = "Талия",
                        value = data?.waistSize,
                        suffix = "см",
                        hint = "Введите значение",
                        id = "waist_size",
                        placeHolder = "Опционально"
                    ),
                    ProfileRangeViewModel(
                        title = "Бедра",
                        value = data?.thighSize,
                        suffix = "см",
                        hint = "Введите значение",
                        id = "thigh_size",
                        placeHolder = "Опционально"
                    ),
                    ProfileRangeViewModel(
                        title = "Рука",
                        value = data?.armSize,
                        suffix = "см",
                        hint = "Введите значение",
                        id = "arm_size",
                        placeHolder = "Опционально"
                    ),
                    ProfileWhiteSpaceViewModel()
                )
            )
            statisticsItems.postValue(adapterData)
        }
    }

    private fun createDate(date: String?): String {
        val fromISO = date?.dateFrom_ISO()
        return fromISO?.to_HH_mm_dd_MM_yyyy_String() ?: run {
            val d = Date()
            val dateString = d.to_HH_mm_dd_MM_yyyy_String()!!
            //TODO:убрать Z после того как поправят на сервере
            putField("created_at", "${d.ISOfromDate()}Z")
            return@run dateString
        }
    }

    fun putField(id: String?, value: CharSequence?) {
        if (id != null && value != null) {
            val number = value.toString().toIntOrNull()
            fieldsMap[id] = number ?: value.toString()
        }
    }

    /**
     * Метод для сохранения отредактированных полей
     */
    fun save() {
        fieldsMap["photos_url"] = urls
        compositeDisposable.add(
            api.patchStatisticsEntry(fieldsMap, data!!.id)
                .doOnError(errorLiveData)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                    { popBackStackIsSuccess.postValue(AlertText.SUCCESS_EDITED.value) },
                    {})
        )
    }

    /**
     * Метод для создания статистики
     */
    fun done() {
        if (fieldsMap.containsKey("target_id")) {
            fieldsMap["photos_url"] = urls
            compositeDisposable.add(
                api.addStatisticsEntry(fieldsMap)
                    .doOnNext {
                        popBackStackIsSuccess.postValue(AlertText.SUCCESS_ADDED.value)
                    }
                    .doOnError(errorLiveData)
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({},
                        {
                            it
                        })
            )
        } else {
            errorLiveData.accept(Throwable(AlertText.WARNING_SELECT_EXERCISE_TRAINING.value))
        }
    }

    fun delete() {
        compositeDisposable.add(
            api.deleteStatisticsEntry(data!!.id)
                .doOnNext {
                    popBackStackIsSuccess.postValue(AlertText.SUCCESS_DELETED.value)
                }
                .doOnError(errorLiveData)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({

                }, {
                    it
                })
        )
    }

    override fun onTargetChange(id: String, type: TargetType, name: String) {
        /*
        * "target_type": "NONE",
        * "target_id": "string"
        * */
        putField("target_id", id)
        putField("target_type", type.toString())
        statisticsItems.value?.getOrNull(1)?.let {
            it as ProfileArrowViewModel
            it.title = name
            statisticsItems.triggerSelf()
        }
    }

    override fun fileLoaded(file: File, fileMediaType: MediaType?) {
        compositeDisposable.add(
            ImagePicker.uploadFile(api, file, fileMediaType, { url ->
                if (!urls.contains(url)) {
                    urls.add(url)
                    imagesUrl.postValue(url)
                }
            }, { throwable ->
                throwable.printStackTrace()
            })
        )
    }

    override fun bitmapLoaded(bitmap: Bitmap) {
    }

    override fun resizedBitmapLoaded(bitmap: Bitmap) {
    }

    override fun onCleared() {
        super.onCleared()
        TrainingTarget.removeTargetListener()
    }
}

enum class AlertText(val value: String) {
    SUCCESS_ADDED("Статистика успешно добавлена"),
    SUCCESS_EDITED("Статистика успешно измененна"),
    SUCCESS_DELETED("Статистика успешно удалена"),
    WARNING_SELECT_EXERCISE_TRAINING("Пожалуйста, выберите упражнение или тренировку")
}
