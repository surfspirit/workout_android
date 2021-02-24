package ru.workout24.ui.exercises_execution

import androidx.navigation.NavController
import ru.workout24.R
import ru.workout24.ui.statistics_layer.edit_statistics_entry.EditStatisticsEntryFragment
import ru.workout24.ui.statistics_layer.edit_statistics_entry.data.dto.StatisticsEntryResponseDto
import ru.workout24.ui.statistics_layer.statistics.StatisticsDateFragment

class ExercisesExecutionHostRouteController(private val navController: NavController, private val parentNavController: NavController?) {
    fun moveToExerciseTimerHostFragment() {
        navController.navigate(R.id.action_beginCountDownFragment_to_exerciseTimerHostFragment)
    }

    fun moveToLikePickerFragment() {
        navController.navigate(R.id.action_exerciseTimerHostFragment_to_likePickerFragment)
    }

    fun moveToHardnessPickerFragment() {
        navController.navigate(R.id.action_likePickerFragment_to_hardnessPickerFragment)
    }

    fun moveToSaveResultsFragment() {
        navController.navigate(R.id.action_hardnessPickerFragment_to_saveResultsFragment2)
    }

    fun moveToGlobalSaveResultsFragment() {
        navController.navigate(R.id.action_global_saveResultsFragment)
    }

    fun moveToEditStatisticFragment(statisticsEntry: StatisticsEntryResponseDto) {
        navController.popBackStack(R.id.beginCountDownFragment, true)
        navController.setGraph(R.navigation.statistics_layer)
        navController.navigate(R.id.action_global_editStatisticsEntryFragment, EditStatisticsEntryFragment.getEditStatisticsEntryBundle(statisticsEntry.convertToUserStatistic(), StatisticsDateFragment.DateItemType.ITEM_AUTO))
    }

    fun moveToFinishTestFragment() {
        parentNavController?.navigate(R.id.action_exercisesExecutionHostFragment_to_testFinishedFragment)
    }

    fun closeAll() {
        parentNavController?.popBackStack()
    }
}

class ExerciseTimerHostRouteController(
    private val navController: NavController
) {
    fun moveToDescriptionFragment() {
        navController.navigate(R.id.descriptionFragment)
    }

    fun moveToResultPickerFragment() {
        navController.navigate(R.id.resultPickerFragment)
    }
}

