package ru.workout24.ui.exercises_execution.fragments.exercise_timer_fragment.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_result_picker.*
import ru.workout24.R
import ru.workout24.ui.exercises_execution.ExercisesExecutionViewModel
import ru.workout24.utills.base.OnBackPressedListener

class ResultPickerFragment : Fragment(R.layout.fragment_result_picker) {
    private val viewModel by lazy {
        ViewModelProviders.of(requireParentFragment().requireParentFragment())[ExercisesExecutionViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repeatCount = viewModel.currentExercise.exerciseTrainingProgram.repeatCount
        wheelPicekr.data = (1..repeatCount).toMutableList()
        wheelPicekr.setOnItemSelectedListener { _, data, _ ->
            viewModel.saveCount(data as Int)
        }
    }
}