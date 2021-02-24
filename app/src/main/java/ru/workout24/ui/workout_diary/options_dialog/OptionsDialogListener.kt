package ru.workout24.ui.workout_diary.options_dialog

import ru.workout24.ui.workout_diary.data.model.WorkoutViewModel.*

interface OptionsDialogListener {
    /*
    fun moveOnNextWeek(item: TrainingViewModel)
    fun endProgram(item: ProgramHeaderViewModel)
    fun seeProgramResult(item: TrainingViewModel)
    fun watchProgram(item: AbstractTypeViewModel)
    fun deleteFromList(item: TrainingViewModel)
    */
    fun seeProgram(item: ProgramHeaderViewModel)
    fun endProgram(item: ProgramHeaderViewModel)

    fun seeExerciseResult(item: TrainingViewModel)

    fun seeSingleTrainingResult(item: TrainingViewModel)

    fun seeUpcomingProgramTraining(item: TrainingViewModel)
    fun seeDoneProgramTrainingResult(item: TrainingViewModel)
    fun moveLostProgramTraining(item: TrainingViewModel)
    fun removeProgramTraining(item: TrainingViewModel)
    fun seeTrainingProgram(item: TrainingViewModel)
}