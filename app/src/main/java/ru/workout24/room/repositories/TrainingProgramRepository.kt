package ru.workout24.room.repositories

import android.content.Context
import ru.workout24.network.Api
import ru.workout24.network.BaseResponse
import ru.workout24.room.AppDatabase
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgram
import ru.workout24.utills.NetworkBoundResource
import io.reactivex.Flowable
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingProgramRepository @Inject constructor(
    private val context: Context,
    private val api: Api,
    private val db: AppDatabase
) {
   fun getProgramById(id:String?) = object : NetworkBoundResource <TrainingProgram, BaseResponse<TrainingProgram>>(context){
           override fun saveCallResult(request: BaseResponse<TrainingProgram>) {
               db.daoTrainingProgram.insert(request.data)
           }

           override fun loadFromDb(): Flowable<TrainingProgram>  {
              return db.daoTrainingProgram.getById(id)
           }
               override fun createCall(): Flowable<Response<BaseResponse<TrainingProgram>>> = api.getTrainingProgramById(id)
           }
       }


