package ru.workout24.ui.exercises_execution.fragments.exercise_timer_fragment.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_process_exercise1.*
import ru.workout24.R
import ru.workout24.ui.exercises_execution.ExercisesExecutionViewModel
import ru.workout24.utills.base.OnBackPressedListener
import ru.workout24.utills.hide

class DescriptionFragment : Fragment(R.layout.fragment_process_exercise1) {
    private val viewModel by lazy {
        ViewModelProviders.of(requireParentFragment().requireParentFragment())[ExercisesExecutionViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initExerciseDescription()
    }

    private fun initExerciseDescription() {
        val current = viewModel.currentExercise
        val all = viewModel.currentSet.allExercises
        exerciseName.text = current.exerciseTrainingProgram.name
        if (!current.exerciseTrainingProgram.previewUrl.isNullOrEmpty()) Glide.with(exerciseUrlImage)
            .load(current.exerciseTrainingProgram.previewUrl).into(exerciseUrlImage)
        exerciseRepeatCount.text = "${current.exerciseTrainingProgram.repeatCount} повторов"
        if (viewModel.isShowProgressView) {
            progressView.all = all
            progressView.current = current.position
            progressView.drawProgress()
        } else {
            progressView.hide()
        }
    }
}