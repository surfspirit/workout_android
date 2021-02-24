package ru.workout24.room.repositories


import android.content.Context
import ru.workout24.network.*
import ru.workout24.room.AppDatabase
import ru.workout24.ui.trainings.once_exercise.Inventories
import ru.workout24.ui.trainings.once_exercise.MuscleGroup
import ru.workout24.ui.trainings.once_exercise.OnceExercise
import ru.workout24.utills.NetworkBoundResource
import com.google.gson.Gson
import io.reactivex.Flowable
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnceExerciseRepository @Inject constructor(
    private val context: Context,
    private val api: Api,
    private val db: AppDatabase
) {

    fun getInventory() = object : NetworkBoundResource<List<Inventories>, BaseResponse<InventoryResponse>>(context) {
        override fun saveCallResult(request: BaseResponse<InventoryResponse>) {
            request.data?.let {
                db.daoInventories.insertList(it.inventories)
            }
        }

        override fun loadFromDb(): Flowable<List<Inventories>> = db.daoInventories.getAll()

        override fun createCall(): Flowable<Response<BaseResponse<InventoryResponse>>> = api.getInventories()

    }

    fun getMuscleGroups() =
        object : NetworkBoundResource<List<MuscleGroup>, BaseResponse<MuscleGroupResponse>>(context) {
            override fun saveCallResult(request: BaseResponse<MuscleGroupResponse>) {
                request.data?.let {
                    db.daoMuscleGroup.insertList(it.muscle_groups)
                }
            }

            override fun loadFromDb(): Flowable<List<MuscleGroup>> = db.daoMuscleGroup.getAll()

            override fun createCall(): Flowable<Response<BaseResponse<MuscleGroupResponse>>> = api.getMuscleGroups()

        }

    fun getOnceExercises(inventory: FilterQueryParam? = null, muscle_groups: FilterQueryParam? = null, time :String? = null) =
        object : NetworkBoundResource<List<OnceExercise>, BaseResponse<List<OnceExercise>>>(context) {
            override fun saveCallResult(request: BaseResponse<List<OnceExercise>>) {
                request.data?.let { db.daoOnceExercise.insertList(it) }
            }

            override fun loadFromDb(): Flowable<List<OnceExercise>> = db.daoOnceExercise.getAll()

            override fun createCall(): Flowable<Response<BaseResponse<List<OnceExercise>>>> = api.getOnceExercises(
                if (inventory != null) Gson().toJson(inventory) else null,
                if (muscle_groups != null) Gson().toJson(muscle_groups) else null,
                if (!time.isNullOrEmpty()) time else null
            )


        }


}
