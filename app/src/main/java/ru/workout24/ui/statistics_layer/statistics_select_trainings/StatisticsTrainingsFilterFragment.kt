package ru.workout24.ui.statistics_layer.statistics_select_trainings

import android.os.Bundle
import android.view.View
import ru.workout24.R
import ru.workout24.ui.statistics_layer.statistics_select_trainings.adapter.ViewPagerAdapter
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_statistics_trainings_filter.*

/**
 * Экран вью-педжер "Программа тренировки" для экранов 7 и 8
 * @see https://docs.google.com/document/d/19YAzjzHIWNR-GghihyCXdi7S6p09m72D/edit#heading=h.1g7p69mvimf3
 */
class StatisticsTrainingsFilterFragment : BaseFragment(R.layout.fragment_statistics_trainings_filter) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBar.setOnBackClick {
            controller.popBackStack()
        }
        tabs.setupWithViewPager(viewPager)
        viewPager.adapter = ViewPagerAdapter(childFragmentManager)
    }


}