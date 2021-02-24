package ru.workout24.room.repositories


import android.content.Context
import ru.workout24.network.Api
import ru.workout24.network.BaseResponse
import ru.workout24.room.AppDatabase
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgramFilter
import ru.workout24.utills.NetworkBoundResource
import io.reactivex.Flowable
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingProgramFilterRepository @Inject constructor(
    private val context: Context,
    private val api: Api,
    private val db: AppDatabase
) {
    val filters =
        object : NetworkBoundResource<List<TrainingProgramFilter>, BaseResponse<List<TrainingProgramFilter>>>(context) {
            override fun saveCallResult(request: BaseResponse<List<TrainingProgramFilter>>) {
                request.data?.let { db.daoTrainingProgramFilter.insertList(it) }
            }

            override fun loadFromDb(): Flowable<List<TrainingProgramFilter>> = db.daoTrainingProgramFilter.getAll()

            override fun createCall(): Flowable<Response<BaseResponse<List<TrainingProgramFilter>>>> =
                api.getTrainingProgramFilters()


        }

}
