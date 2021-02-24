package ru.workout24.room.repositories

import android.content.Context
import ru.workout24.network.Api
import ru.workout24.room.AppDatabase
import ru.workout24.network.ResourceProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingProgramShortRepository @Inject constructor(
    private val context: Context,
    private val api: Api,
    private val db: AppDatabase,
    private val provider: ResourceProvider
) {
//    val shortPrograms = object :
//        NetworkBoundResource<List<TrainingProgramShortDescription>, BaseResponse<List<TrainingProgramShortDescription>>>(
//            context
//        ) {
//        override fun saveCallResult(request: BaseResponse<List<TrainingProgramShortDescription>>) {
//            request.data?.let { db.daoTrainingProgramsShort.insertList(it) }
//        }
//
//        override fun loadFromDb(): Flowable<List<TrainingProgramShortDescription>> =
//            db.daoTrainingProgramsShort.getAll()
//
//        override fun createCall(): Flowable<Response<BaseResponse<List<TrainingProgramShortDescription>>>> {
//            val userGender = provider.userResource.liveData.value?.gender ?: GENDER.NONE
//            return api.getTrainingProgramShortDescription(userGender.toString())
//        }
//
//
//    }

}
