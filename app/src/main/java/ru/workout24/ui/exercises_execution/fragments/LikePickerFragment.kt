package ru.workout24.ui.exercises_execution.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_like_training_picker.*
import ru.workout24.R
import ru.workout24.ui.exercises_execution.ExercisesExecutionViewModel

class LikePickerFragment: Fragment(R.layout.fragment_like_training_picker) {
    private val viewModel by lazy {
        ViewModelProviders.of(requireParentFragment())[ExercisesExecutionViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wheelPicker.data = mutableListOf("Да", "Нет")
        wheelPicker.setOnItemSelectedListener { _, data, _ ->
            viewModel.saveLike((data as String) == "Да")
        }
        btn_like_cont.setOnClickListener {
            viewModel.moveToHardnessPickerFragment()
        }
        appBarLayout.setOnBackClick {
            viewModel.closeAll()
        }
    }
}