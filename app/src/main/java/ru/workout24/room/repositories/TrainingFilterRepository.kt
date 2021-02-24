package ru.workout24.room.repositories


import android.content.Context
import ru.workout24.network.Api
import ru.workout24.network.BaseResponse
import ru.workout24.room.AppDatabase
import ru.workout24.ui.trainings.once_trainings.pojos.OnceTrainingFilter
import ru.workout24.utills.NetworkBoundResource
import io.reactivex.Flowable
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingFilterRepository @Inject constructor(
    private val context: Context,
    private val api: Api,
    private val db: AppDatabase
) {
    val filters =
        object : NetworkBoundResource<List<OnceTrainingFilter>, BaseResponse<List<OnceTrainingFilter>>>(context) {
            override fun saveCallResult(request: BaseResponse<List<OnceTrainingFilter>>) {
                request.data?.let { db.daoTrainingFilter.insertList(it) }
            }

            override fun loadFromDb(): Flowable<List<OnceTrainingFilter>> = db.daoTrainingFilter.getAll()

            override fun createCall(): Flowable<Response<BaseResponse<List<OnceTrainingFilter>>>> =
                api.getOnceTrainingFilters()


        }

}
