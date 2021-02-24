package ru.workout24.ui.exercises_execution.fragments.exercise_timer_fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.android.synthetic.main.fragment_exercise_timer1.*
import kotlinx.android.synthetic.main.fragment_exercise_timer1.view.*
import org.jetbrains.anko.textColor
import ru.workout24.R
import ru.workout24.ui.exercises_execution.ExerciseTimerHostRouteController
import ru.workout24.ui.exercises_execution.ExercisesExecutionViewModel
import ru.workout24.ui.exercises_execution.TimerState
import ru.workout24.utills.base.OnBackPressedListener

internal class ExerciseTimerHostFragment : NavHostFragment() {
    private lateinit var viewModel: ExercisesExecutionViewModel

    override fun onCreateNavController(navController: NavController) {
        super.onCreateNavController(navController)
        navController.setGraph(R.navigation.exercise_timer_elements_layer)

        val routeController = ExerciseTimerHostRouteController(navController)
        viewModel =
            ViewModelProviders.of(requireParentFragment())[ExercisesExecutionViewModel::class.java]
        viewModel.exerciseTimerRouteController = routeController
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_timer1, container, false)
        view.frameHost.id = id
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.processTimerData.observe(viewLifecycleOwner, Observer { seconds ->
            txt_timer.text = seconds
        })
        viewModel.pauseTimerData.observe(viewLifecycleOwner, Observer { seconds ->
            tv_relax_timer.text = seconds
        })
        viewModel.startedTimerState.observe(viewLifecycleOwner, Observer { type ->
            if (type != null) {
                when (type) {
                    TimerState.PROCESS -> {
                        btn_end_exercise.setText("Завершить упражнение")
                        btn_end_exercise.setOnClickListener { viewModel.moveToResultPickerFragment() }
                    }
                    TimerState.PAUSE -> {
                        btn_end_exercise.setText("Следующее упражнение")
                        btn_end_exercise.setOnClickListener { viewModel.moveToDescriptionFragment() }
                    }
                }
            }
        })
        viewModel.isPauseTimerVisible.observe(viewLifecycleOwner, Observer { isVisible ->
            tv_relax_timer.isVisible = isVisible
            if (isVisible) txt_timer.textColor =
                ContextCompat.getColor(requireContext(), R.color.coral)
            else txt_timer.textColor = Color.WHITE
        })
        viewModel.showProgressDialog.observe(viewLifecycleOwner, Observer {
            btn_end_exercise.isEnable = !it
            btn_end_exercise.showProgress(it)
        })
        viewModel.showApiError.observe(viewLifecycleOwner, Observer {
            if (it) {

            } else {

            }
        })
        viewModel.startProcessTimer()
        appBarLayout.setOnBackClick {
            viewModel.closeAll()
        }
    }
}