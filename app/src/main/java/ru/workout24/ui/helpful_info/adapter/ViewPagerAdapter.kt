package ru.workout24.ui.helpful_info.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.workout24.ui.helpful_info.articles_list.ArticlesListFragment
import ru.workout24.ui.helpful_info.articles_list.ArticlesType

class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
    fm,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
    override fun getItem(position: Int): Fragment = when (position) {
        0 -> ArticlesListFragment.newInstance(ArticlesType.ABOUT_SPORT)
        1 -> ArticlesListFragment.newInstance(ArticlesType.ABOUT_FOOD)
        else -> throw IllegalStateException("Нет такой позиции")
    }

    override fun getCount(): Int = 2

    override fun getPageTitle(position: Int): CharSequence? = when (position) {
        0 -> "о спорте"
        1 -> "о питании"
        else -> throw IllegalStateException("Нет такой позиции")
    }
}