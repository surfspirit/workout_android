package ru.workout24.ui.exercises_execution.fragments

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.fragment_begin_training_countdown.*
import org.jetbrains.anko.textColor
import ru.workout24.R
import ru.workout24.features.CustomSoundsPlayer
import ru.workout24.ui.exercises_execution.ExercisesExecutionViewModel
import ru.workout24.ui.exercises_execution.ProcessTimer

internal class BeginCountDownFragment : Fragment(R.layout.fragment_begin_training_countdown),
    ProcessTimer.TimerListener {
    private val sound by lazy {
        CustomSoundsPlayer(requireContext()).apply {
            isLooping = false
        }
    }
    private val viewModel by lazy {
        ViewModelProviders.of(requireParentFragment())[ExercisesExecutionViewModel::class.java]
    }
    private val timer by lazy {
        ProcessTimer(requireContext(), this)
    }
    private val coral by lazy {
        ContextCompat.getColor(requireContext(), R.color.coral)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timer.start()
    }

    override fun onResume() {
        super.onResume()
        timer.start()
    }

    override fun onPause() {
        super.onPause()
        timer.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer.removeListener()
    }

    override fun onTimerStart() {

    }

    override fun onTickTime(time: String, seconds: Long) {
        when (seconds) {
            1L -> {
                txt_cnt_1.textColor = coral
                sound.playSound(CustomSoundsPlayer.Companion.SOUNDS.THREE_SECONDS_START)
            }
            2L -> {
                txt_cnt_2.textColor = coral
                sound.playSound(CustomSoundsPlayer.Companion.SOUNDS.THREE_SECONDS_START)
            }
            3L -> {
                txt_cnt_3.textColor = coral
                sound.playSound(CustomSoundsPlayer.Companion.SOUNDS.THREE_SECONDS_START)
            }
            4L -> {
                viewModel.moveToExerciseTimerHostFragment()
            }
        }
    }

    override fun onTimerStop() {
        sound.stopSound()
    }
}