package ru.workout24.ui.statistics_layer.statistics_select_trainings.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.workout24.ui.statistics_layer.statistics_select_trainings.TrainingsOrOnceExercisesFilterFragment

/**
 * Адаптер для [StatisticsTrainingsFilterFragment]
 */
class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
    fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getCount(): Int = 2

    override fun getItem(i: Int): Fragment {
        return when (i) {
            0 -> TrainingsOrOnceExercisesFilterFragment.newInstance(TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE.TRAININGS)
            else -> TrainingsOrOnceExercisesFilterFragment.newInstance(TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE.ONCE_EXERCISES)
        }
    }

    override fun getPageTitle(i: Int): CharSequence {
        return when (i) {
            0 -> "Тренировки"
            else -> "Упражнения"
        }
    }
}