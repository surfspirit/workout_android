package ru.workout24.ui.trainings.training_programs.program

import android.content.Context
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import ru.workout24.network.*
import ru.workout24.room.repositories.TrainingProgramRepository
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgram
import ru.workout24.utills.Preferences
import ru.workout24.utills.Resource
import ru.workout24.utills.base.BaseViewModel
import ru.workout24.utills.toArrayList
import ru.workout24.utills.triggerSelf
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.wtf
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class VMTrainingProgram @Inject constructor(
    private val api: Api,
    private val rep: TrainingProgramRepository,
    private val context: Context,
    private val preferences: Preferences
) : BaseViewModel() {

    val trainingProgram = MutableLiveData<Resource<TrainingProgram>>()
    val userSubs = MutableLiveData<Resource<List<UserSubscription>>>()
    val message_start = MutableLiveData<String>()
    val message_end = MutableLiveData<String>()
    fun getProgramById(id: String?) {
        compositeDisposable.add(
            //rep.getProgramById(id).asFlowable().subscribeLiveData(trainingProgram)
            rep.getProgramById(id).asFlowable().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({
                trainingProgram.postValue(it)
                wtf("POSTED " + it.getSuccessResult()?.name)
            }, {
                //errorCallback(it)
            })
        )
    }

    fun startTrainingSet() {
        api.startTrainingSet(training_set_id = trainingProgram.value?.getSuccessResult()?.id)
            .enqueue(object : Callback<BaseResponse<MessageResponse>> {
                override fun onFailure(call: Call<BaseResponse<MessageResponse>>, t: Throwable) {
                    message_start.value = "failure"
                }

                override fun onResponse(
                    call: Call<BaseResponse<MessageResponse>>,
                    response: Response<BaseResponse<MessageResponse>>
                ) {
                    if (response.code() in 200..299) {
                        message_start.value = response.body()?.data?.message
                        val list = preferences.training_sets.split(",").toArrayList()
                        list.add(trainingProgram.value?.getSuccessResult()?.id!!)
                        preferences.training_sets = TextUtils.join(",", list)
                        trainingProgram.triggerSelf()

                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<BaseResponseWithoutData>() {}.type
                        val errorResponse: BaseResponseWithoutData? =
                            gson.fromJson(response.errorBody()!!.charStream(), type)
                        message_start.value = errorResponse?.errorMessage
                    }
                }
            })
    }

    fun endTrainingSet() {
        api.endTrainingSet(training_set_id = trainingProgram.value?.getSuccessResult()?.id)
            .enqueue(object : Callback<BaseResponse<MessageResponse>> {
                override fun onFailure(call: Call<BaseResponse<MessageResponse>>, t: Throwable) {
                    message_end.value = "failure"
                }

                override fun onResponse(
                    call: Call<BaseResponse<MessageResponse>>,
                    response: Response<BaseResponse<MessageResponse>>
                ) {
                    if (response.code() in 200..299) {
                        message_end.value = response.body()?.data?.message
                        val list = preferences.training_sets.split(",").toArrayList()
                        list.remove(trainingProgram.value?.getSuccessResult()?.id!!)
                        preferences.training_sets = TextUtils.join(",", list)
                        trainingProgram.triggerSelf()
                    } else {
                        val gson = Gson()
                        val type = object : TypeToken<BaseResponseWithoutData>() {}.type
                        val errorResponse: BaseResponseWithoutData? =
                            gson.fromJson(response.errorBody()!!.charStream(), type)
                        message_end.value = errorResponse?.errorMessage
                    }
                }
            })
    }


}
