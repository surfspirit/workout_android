package ru.workout24.ui.workout_diary_planing_done.diary_planing_done_training

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.workout24.R
import ru.workout24.ui.workout_diary.data.dto.TrainingDto
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_diary_done_training.*
import ru.workout24.ui.workout_diary.data.model.TrainingType

class DiaryPlaningDoneTrainingFragment : BaseFragment(R.layout.fragment_diary_done_training) {
    private val type get() = arguments!!.getSerializable(TRAINING_TYPE) as TrainingType
    private val hasResult get() = type == TrainingType.DONE
    private val data get() = arguments!!.getParcelable<TrainingDto>(TRAINING_KEY)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.setOnBackClick {
            controller.popBackStack()
        }

        val adapter = DiaryDoneTrainingsAdapter(childFragmentManager)
        vp_diary_trainings_pager.adapter = adapter
        tl_diary_done_training_tab.setupWithViewPager(vp_diary_trainings_pager)
        val title = if (hasResult) "Выполненная тренировка" else "Запланированная тренировка"
        appBarLayout.setTitleText(title)
        if (hasResult) {
            vp_diary_trainings_pager.currentItem = 1
        }
    }

    companion object {
        const val TRAINING_TYPE = "TRAINING_TYPE"
        const val TRAINING_KEY = "TRAINING_KEY"

        fun getBundle(type: TrainingType, data: TrainingDto): Bundle {
            return Bundle().apply {
                putSerializable(TRAINING_TYPE, type)
                putParcelable(TRAINING_KEY, data)
            }
        }
    }

    inner class DiaryDoneTrainingsAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

        override fun getCount(): Int = 2

        override fun getItem(i: Int): Fragment {
            return when (i) {
                0 -> {
                    DiaryDoneTrainingPlanFragment.newInstance(hasResult, data)
                }
                1 -> {
                    DiaryDoneTrainingResultsFragment.newInstance(hasResult, data)
                }
                else -> DiaryDoneTrainingPlanFragment.newInstance(hasResult, data)
            }
        }

        override fun getPageTitle(i: Int): CharSequence {
            return when (i) {
                0 -> "План"
                1 -> "Результаты"
                else -> "План"
            }
        }
    }
}