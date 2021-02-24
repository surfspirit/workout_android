package ru.workout24.ui.auth_layer.test_layer

import androidx.lifecycle.MutableLiveData
import ru.workout24.ui.auth_layer.test_layer.pojos.Exercise
import ru.workout24.utills.base.BaseViewModel
import ru.workout24.utills.responseCheck
import ru.workout24.utills.toArrayList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.error
import ru.workout24.network.*
import ru.workout24.ui.trainings.training_programs.pojos.ExerciseTrainingProgram
import ru.workout24.ui.trainings.training_programs.pojos.Set
import javax.inject.Inject

class VMFitnessTest @Inject constructor(val api: Api, val resourceProvider: ResourceProvider) : BaseViewModel() {
    val exercises = MutableLiveData<ArrayList<Exercise>>()
    val exerciseSet get() = exercises.value?.mapExerciseToSetWithExerciseTrainingProgram()

    fun getExercises() {
        compositeDisposable.add(
            api.getExercises()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ responce ->
                    responseCheck(responce, {
                        exercises.value = it?.data?.filterNotNull()?.toArrayList()
                    }, { errorCode, message ->
                        sendMessage(-1, message)
                    })

                }, { throwable ->
                    error { throwable }
                })
        )
    }

    private fun ArrayList<Exercise>.mapExerciseToSetWithExerciseTrainingProgram(): ArrayList<Set> {
        return arrayListOf(Set(
            -1,
            map { exercise ->
                ExerciseTrainingProgram(
                    description = exercise.description,
                    estimateTime = exercise.requiredTime?.toInt(),
                    exerciseId = exercise.id,
                    id = exercise.id ?: "",
                    inventory = exercise.inventory,
                    muscle_groups = exercise.muscle_groups,
                    name = exercise.name,
                    pauseTime = exercise.pauseTime,
                    previewUrl = exercise.previewUrl,
                    requiredRange = exercise.requiredRange,
                    videoUrl = exercise.videoUrl
                )
            }
        ))
    }
}