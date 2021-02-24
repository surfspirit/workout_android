package ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.mapper

import androidx.arch.core.util.Function
import ru.workout24.ui.trainings.training_programs.pojos.ExerciseTrainingProgram
import ru.workout24.ui.trainings.training_programs.pojos.Training
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.DiaryDoneTrainingResultsFragment
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.data.dto.TrainingExerciseResultDto
import ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training.data.dto.TrainingResultMetaDto


class TrainingResultMapper :
    Function<Triple<Training?, List<TrainingExerciseResultDto>?, TrainingResultMetaDto?>, List<DiaryDoneTrainingResultsFragment.DoneTrainingAdapterItem>> {
    override fun apply(input: Triple<Training?, List<TrainingExerciseResultDto>?, TrainingResultMetaDto?>): List<DiaryDoneTrainingResultsFragment.DoneTrainingAdapterItem> {
        return if (input.first != null && input.second != null) {
            val training = input.first
            val exercisesResult = input.second
            val metaResult = input.third
            val like = if (metaResult?.like == true) {
                "Понравилась"
            } else {
                "Не понравилась"
            }
            val elements = arrayListOf<DiaryDoneTrainingResultsFragment.DoneTrainingAdapterItem>(
                DiaryDoneTrainingResultsFragment.DoneTrainingItem(
                    "Нагрузка",
                    metaResult?.difficulty?.value ?: ""
                ),
                DiaryDoneTrainingResultsFragment.DoneTrainingItem("Тренировка", like)
            )
            elements.addAll(mapExercisesWithResults(training!!, exercisesResult!!))
            elements
        } else {
            emptyList()
        }
    }

    private fun mapExercisesWithResults(
        t: Training,
        r: List<TrainingExerciseResultDto>
    ): List<DiaryDoneTrainingResultsFragment.DoneTrainingAdapterItem> {
        val result = arrayListOf<DiaryDoneTrainingResultsFragment.DoneTrainingAdapterItem>()
        val exercisesWithResult = arrayListOf<ExerciseWithResultDto>()
        t.sets.forEachIndexed { setIndex, set ->
            set?.exercises?.forEach { exercise ->
                exercisesWithResult.add(ExerciseWithResultDto(
                    setIndex + 1,
                    exercise,
                    r.find { result -> exercise.id == result.trainingExerciseId }
                ))
            }
        }
        exercisesWithResult.groupBy { exerciseWithResult ->
            exerciseWithResult.exercise.exerciseId
        }.apply {
            for (mapEntry in entries) {
                var totalExercisesTime = 0
                mapEntry.value.forEach { resultWithIndex ->
                    totalExercisesTime += resultWithIndex.result?.time ?: 0
                }
                result.add(
                    DiaryDoneTrainingResultsFragment.DoneTrainingHeader(
                        exercisesWithResult.find { it.exercise.exerciseId == mapEntry.key }?.exercise?.name
                            ?: "", formatTime(totalExercisesTime)
                    )
                )
                mapEntry.value.forEach { resultWithIndex ->
                    result.add(
                        DiaryDoneTrainingResultsFragment.DoneTrainingItem(
                            "Подход ${resultWithIndex.setCount}/${mapEntry.value.size}",
                            "x${resultWithIndex.result?.count.toString()}"
                        )
                    )
                }
            }
        }
        return result
    }

    private fun formatTime(secs: Int): String {
        return String.format("%02d:%02d", secs % 3600 / 60, secs % 60)
    }
}

internal data class ExerciseWithResultDto(
    val setCount: Int,
    val exercise: ExerciseTrainingProgram,
    val result: TrainingExerciseResultDto?
)