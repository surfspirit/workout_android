package ru.workout24.ui.trainings.training_programs.training

import android.content.Context
import androidx.lifecycle.MutableLiveData
import ru.workout24.network.Api
import ru.workout24.network.BaseResponse
import ru.workout24.network.StartTrainingResponse
import ru.workout24.room.AppDatabase
import ru.workout24.room.repositories.TrainingByIdRepository
import ru.workout24.ui.trainings.training_programs.pojos.Training
import ru.workout24.utills.Resource
import ru.workout24.utills.base.BaseViewModel
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class VMAboutTraining @Inject constructor(
    private val api: Api,
    private val db: AppDatabase,
    private val context: Context,
    private var rep : TrainingByIdRepository
    ) : BaseViewModel() {

    val training = MutableLiveData<Resource<Training>>()
    val message = MutableLiveData<String>()
    fun getTrainingById(id: String?) {
        compositeDisposable.add(
            //rep.getProgramById(id).asFlowable().subscribeLiveData(trainingProgram)
            rep.getProgramById(id).asFlowable().subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe({
                training.postValue(it)
            }, {
                //errorCallback(it)
            })
        )
    }

    fun startTraining()
    {
        api.startTraining(training.value?.getSuccessResult()?.id).enqueue( object : Callback<BaseResponse<StartTrainingResponse>>
        {
            override fun onFailure(call: Call<BaseResponse<StartTrainingResponse>>, t: Throwable) {
                message.value = "failure"
            }

            override fun onResponse(call: Call<BaseResponse<StartTrainingResponse>>, response: Response<BaseResponse<StartTrainingResponse>>) {
                if (response.code() in 200..299)
                {
                    message.value = response.body()?.data?.trainingAssignmentId
                }
                else
                    message.value = "failure"

            }
        })
    }

}