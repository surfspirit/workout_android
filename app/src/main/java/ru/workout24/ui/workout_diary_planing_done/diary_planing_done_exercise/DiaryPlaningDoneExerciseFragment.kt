package ru.workout24.ui.workout_diary_planing_done.diary_planing_done_exercise

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.workout24.R
import ru.workout24.ui.auth_layer.test_layer.pojos.Exercise
import ru.workout24.ui.workout_diary.data.dto.SingleExerciseDto
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_diary_done_exercise.*
import ru.workout24.ui.workout_diary.data.model.TrainingType

class DiaryPlaningDoneExerciseFragment : BaseFragment(R.layout.fragment_diary_done_exercise) {

    private val singleExercise get() = arguments!!.getParcelable<SingleExerciseDto>(SINGLE_EXERCISES_KEY)
    private val type get() = arguments!!.getSerializable(TYPE_KEY) as TrainingType
    private val hasResult get() = type == TrainingType.DONE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.setOnBackClick {
            controller.popBackStack()
        }

        val adapter = DiaryDoneExerciseAdapter(childFragmentManager)
        vp_diary_exercise_pager.adapter = adapter
        tl_diary_done_exercise_tab.setupWithViewPager(vp_diary_exercise_pager)
        val title = if (hasResult) "Выполненное упражнение" else "Запланированное упражнение"
        appBarLayout.setTitleText(title)
        if (hasResult) {
            vp_diary_exercise_pager.currentItem = 1
        }
    }


    inner class DiaryDoneExerciseAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int  = 2

        override fun getItem(i: Int): Fragment {
            return when(i){
                0 -> {
                    DiaryDoneExercisePlanFragment.newInstance(singleExercise)
                }
                1 -> {
                    DiaryDoneExerciseResultsFragment.newInstance(singleExercise, hasResult)
                }
                else -> DiaryDoneExercisePlanFragment.newInstance(singleExercise)
            }
        }

        override fun getPageTitle(i: Int): CharSequence {
            return when(i){
                0 -> "План"
                1 -> "Результаты"
                else -> "План"
            }
        }
    }

    data class CalendarSingleExercieseResult(
        val id:String,
        val time:Int,
        val count:Int,
        val created_at:String,
        val exercise: Exercise?
    )

    companion object {
        val SINGLE_EXERCISES_KEY = "single_exercise"
        val TYPE_KEY = "TYPE_KEY"

        fun getBundle(singleExercises: SingleExerciseDto, type: TrainingType) = Bundle().apply {
            putParcelable(SINGLE_EXERCISES_KEY, singleExercises)
            putSerializable(TYPE_KEY, type)
        }
    }
}