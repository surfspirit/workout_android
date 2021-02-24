package ru.workout24.ui.exercises_execution.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_hardness_training_picker.*
import ru.workout24.R
import ru.workout24.ui.exercises_execution.ExercisesExecutionViewModel
import ru.workout24.utills.difficultyEnum
import ru.workout24.utills.getKeyByValue

class HardnessPickerFragment: Fragment(R.layout.fragment_hardness_training_picker) {
    private val DEFAULT_PICKED_POSITION = 0

    private val viewModel by lazy {
        ViewModelProviders.of(requireParentFragment())[ExercisesExecutionViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wheelPicker.data = difficultyEnum.values.toMutableList()
        wheelPicker.selectedItemPosition = DEFAULT_PICKED_POSITION
        wheelPicker.setOnItemSelectedListener { _, value, _ ->
            difficultyEnum.getKeyByValue(value as String)?.let { difficulty -> viewModel.saveHardness(difficulty) }
        }
        btn_hard_cont.setOnClickListener {
            viewModel.moveToSaveResultsFragment()
        }
        appBarLayout.setOnBackClick {
            viewModel.closeAll()
        }
    }
}