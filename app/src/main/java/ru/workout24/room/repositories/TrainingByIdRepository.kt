package ru.workout24.room.repositories


import android.content.Context
import ru.workout24.network.Api
import ru.workout24.network.BaseResponse
import ru.workout24.room.AppDatabase
import ru.workout24.ui.trainings.training_programs.pojos.Training
import ru.workout24.utills.NetworkBoundResource
import io.reactivex.Flowable
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingByIdRepository @Inject constructor(
    private val context: Context,
    private val api: Api,
    private val db: AppDatabase
) {
    fun getProgramById(id:String?) = object : NetworkBoundResource <Training, BaseResponse<Training>>(context){
        override fun saveCallResult(request: BaseResponse<Training>) {
           db.daoTrainings.insert(request.data)
        }

        override fun loadFromDb(): Flowable<Training> = db.daoTrainings.getById(id)

        override fun createCall(): Flowable<Response<BaseResponse<Training>>> =api.getTrainingById(id)

    }
}