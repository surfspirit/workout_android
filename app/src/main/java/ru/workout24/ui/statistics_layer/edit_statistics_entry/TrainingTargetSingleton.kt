package ru.workout24.ui.statistics_layer.edit_statistics_entry

/**
 * Синглтон-костыль чтобы эффективнее передать аругменты из [SelectOnceExerciseFragment]
 * в [EditStatisticsEntryFragment]
 */
object TrainingTarget {
    private var listener: TargetListener? = null

    fun setTarget(id: String, type: TargetType, name: String) {
        listener?.onTargetChange(id, type, name)
    }

    fun setTargetListener(listener: TargetListener) {
        this.listener = listener
    }

    fun removeTargetListener() {
        listener = null
    }
}

interface TargetListener {
    fun onTargetChange(id: String, type: TargetType, name: String)
}

enum class TargetType {
    NONE, TRAINING_RESULT, SINGLE_EXERCISE_RESULTS, TRAINING, SINGLE_EXERCISE
}