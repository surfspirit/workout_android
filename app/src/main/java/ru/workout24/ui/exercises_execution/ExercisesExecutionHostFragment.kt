package ru.workout24.ui.exercises_execution

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import dagger.android.support.AndroidSupportInjection
import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.ui.trainings.training_programs.pojos.ExerciseTrainingProgram
import ru.workout24.ui.trainings.training_programs.pojos.Set
import ru.workout24.utills.NONE_INT
import ru.workout24.utills.NONE_STRING
import ru.workout24.utills.base.OnBackPressedCallback
import javax.inject.Inject


class ExercisesExecutionHostFragment : NavHostFragment(), OnBackPressedCallback {
    @Inject
    lateinit var api: Api

    private val openType get() = requireArguments().getSerializable(OPEN_TYPE_KEY) as OPEN_TYPE
    private val assignmentId get() = requireArguments().getString(ASSIGNMENT_ID_KEY, NONE_STRING)
    private val exercises get() = requireArguments().getParcelableArrayList<Set>(EXERCISES_KEY)

    private val  viewModel by lazy {
        ViewModelProviders.of(
            this,
            // app context == activity context
            ExercisesExecutionViewModelFactory(requireActivity(), api, openType, exercises, assignmentId)
        )[ExercisesExecutionViewModel::class.java]
    }

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateNavController(navController: NavController) {
        super.onCreateNavController(navController)
        navController.setGraph(R.navigation.exercise_execution_layer)

        val parentNavController = try {
            parentFragment?.let { findNavController(it) }
        } catch (e: Exception) {
            null
        }
        viewModel.exercisesExecutionRouteController = ExercisesExecutionHostRouteController(navController, parentNavController)
    }

    override fun onBackPressedCallback() {
        viewModel.stopTimers()
    }

    companion object {
        private const val TRAINING_ID_KEY = "TRAINING_ID_KEY"
        private const val EXERCISES_KEY = "EXERCISES_KEY"
        private const val OPEN_TYPE_KEY = "OPEN_TYPE_KEY"
        private const val BACK_TO_KEY = "BACK_TO_KEY"
        private const val ASSIGNMENT_ID_KEY = "ASSIGNMENT_ID_KEY"

        fun openForExercise(
            exercise: ExerciseTrainingProgram
        ) = getBundle(OPEN_TYPE.EXERCISE).apply {
            putParcelableArrayList(EXERCISES_KEY, arrayListOf(Set(0, listOf(exercise))))
        }

        fun openForTraining(
            assignmentId: String,
            trainingSets: ArrayList<Set>
        ) = getBundle(OPEN_TYPE.TRAINING).apply {
            putString(ASSIGNMENT_ID_KEY, assignmentId)
            putParcelableArrayList(EXERCISES_KEY, trainingSets)
        }

        fun openForProgramTraining(
            assignmentId: String,
            trainingSets: ArrayList<Set>
        ) = getBundle(OPEN_TYPE.PROGRAM_TRAINING).apply {
            putString(ASSIGNMENT_ID_KEY, assignmentId)
            putParcelableArrayList(EXERCISES_KEY, trainingSets)
        }

        fun openForTest(
            trainingSets: ArrayList<Set>
        ) = getBundle(OPEN_TYPE.TEST).apply {
            putParcelableArrayList(EXERCISES_KEY, trainingSets)
        }

        fun getBundle(openType: OPEN_TYPE, backTo: Int = NONE_INT): Bundle = Bundle().apply {
            putSerializable(OPEN_TYPE_KEY, openType)
            putInt(BACK_TO_KEY, backTo)
        }
    }

    enum class OPEN_TYPE {
        PROGRAM_TRAINING,
        EXERCISE,
        TRAINING,
        TEST
    }
}