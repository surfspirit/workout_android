package ru.workout24.ui.auth_layer.anket_layer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.reactivex.Flowable
import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.utills.Preferences
import ru.workout24.utills.base.BaseViewModel
import ru.workout24.utills.triggerSelf
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import ru.workout24.network.BaseResponse
import ru.workout24.network.User
import ru.workout24.ui.statistics_layer.edit_statistics_entry.data.dto.StatisticsEntryResponseDto
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class AnketViewModel @Inject constructor(val api: Api, val pref: Preferences) : BaseViewModel() {

    companion object {
        const val SUCCESS_SEND_CHANGES = 600
        const val SEND_MAIN_PARAMS = 101
        const val SEND_SUB_PARAMS = 102
    }

    data class Changes(
        var weight: Float? = null,
        var height: Float? = null,
        var percent: Float? = null
    )

    val weight = MutableLiveData<Float>()
    val height = MutableLiveData<Float>()
    val percent = MutableLiveData<Float>()
    val trainingLevel = MutableLiveData<Int>()
    val trainingLevelTag: LiveData<String> = Transformations.map(trainingLevel, ::transformIdToTag)

    val trainingGoal = MutableLiveData<Int>()
    val trainingGoalTag: LiveData<String> = Transformations.map(trainingGoal, ::transformIdToTag)
    val trainingCount = MutableLiveData<Int>()
    val trainingCountTag: LiveData<String> = Transformations.map(trainingCount, ::transformIdToTag)

    val changes = MediatorLiveData<Changes>()

    val gender = MutableLiveData<String>()
    val notifications = MutableLiveData<Boolean>()
    val birthdayRowValue = MutableLiveData<Calendar>()
    private val birthdayServer: LiveData<String> = Transformations.map(birthdayRowValue, ::transformCalendarToString)
    val birthdayUI: LiveData<String> = Transformations.map(birthdayRowValue, ::transformCalendarToUiFormat)

    private fun transformIdToTag(int: Int): String {
        when (int) {
            R.id.rb_low -> {
                return "LEVEL_1"
            }
            R.id.rb_beginner -> {
                return "LEVEL_2"
            }
            R.id.rb_medium -> {
                return "LEVEL_3"
            }
            R.id.rb_advanced -> {
                return "LEVEL_4"
            }
            R.id.rb_master -> {
                return "LEVEL_5"
            }
            R.id.rb_first -> {
                return "LOSE_WEIGHT"
            }
            R.id.rb_second -> {
                return "HEALTHY_AND_STRONG"
            }
            R.id.rb_third -> {
                return "MUSCLE_BUILDING"
            }
            R.id.radioButton1 -> {
                return "3"
            }
            R.id.radioButton2 -> {
                return "4"
            }
            R.id.radioButton3 -> {
                return "5"
            }
            else -> return ""
        }
    }

    private fun transformCalendarToString(calendar: Calendar): String {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.ROOT)
        return sdf.format(calendar.time)
    }

    private fun transformCalendarToUiFormat(calendar: Calendar): String {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.ROOT)
        return sdf.format(calendar.time)
    }

    init {
        observeChanges()
        changes.value = Changes()
    }

    private fun observeChanges() {
        changes.addSource(weight) {
            changes.value?.weight = it
            changes.triggerSelf()
        }
        changes.addSource(height) {
            changes.value?.height = it
            changes.triggerSelf()
        }
        changes.addSource(percent) {
            changes.value?.percent = it
            changes.triggerSelf()
        }
    }

    fun sendInfo(int: Int) {
        val data = hashMapOf<String, Any>()
        val statData = hashMapOf<String, Any>("target_type" to "NONE")

        weight.value?.let {
            statData["weight"] = it
        }
        height.value?.let {
            statData["height"] = it
        }
        percent.value?.let {
            statData["fat_percentage"] = it
        }
        trainingLevel.value?.let {
            data["training_level"] = transformIdToTag(it)
        }
        trainingCount.value?.let {
            data["trainings_in_week"] = transformIdToTag(it).toInt()
        }
        trainingGoal.value?.let {
            data["goals"] = transformIdToTag(it)
        }
        gender.value?.let {
            data["gender"] = it
        }
        birthdayRowValue.value?.let {
            data["bdate"] = transformCalendarToString(it)
        }
        notifications.value?.let {
            data["notifications"] = it
        }

        compositeDisposable.add(
            Flowable.zip(
                api.sendUserInfo(data),
                api.addStatisticsEntry(statData),
                BiFunction<Response<BaseResponse<User>>, BaseResponse<StatisticsEntryResponseDto>, Unit> { t1, t2 ->
                }
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    //если запрос прошел считаем что он выполнен
                    sendMessage(int, "")
                }, { throwable ->
                    sendMessage(-1, "Произошла ошибка связи с сервером. Попробуйте еще раз")
//                    error { throwable }
                })
        )
    }

    fun isAllParamsFill(): Boolean {
        return weight.value != null && height.value != null && percent.value != null
    }
}