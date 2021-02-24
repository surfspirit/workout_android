package ru.workout24.room.repositories

import android.content.Context
import ru.workout24.network.Api
import ru.workout24.network.BaseResponse
import ru.workout24.room.AppDatabase
import ru.workout24.ui.trainings.once_trainings.pojos.OnceTraining
import ru.workout24.utills.NetworkBoundResource
import io.reactivex.Flowable
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnceTrainingsRepository @Inject constructor(
    private val context: Context,
    private val api: Api,
    private val db: AppDatabase
) {
    val trainings = object : NetworkBoundResource<List<OnceTraining>, BaseResponse<List<OnceTraining>>>(context) {
        override fun saveCallResult(request: BaseResponse<List<OnceTraining>>) {
            request.data?.let { db.daoOnceTrainings.insertList(it) }
        }

        override fun loadFromDb(): Flowable<List<OnceTraining>> =db.daoOnceTrainings.getAll()

        override fun createCall(): Flowable<Response<BaseResponse<List<OnceTraining>>>> =api.getOnceTrainings()


    }

}
