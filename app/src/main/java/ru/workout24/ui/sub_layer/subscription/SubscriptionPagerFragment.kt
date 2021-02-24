package ru.workout24.ui.sub_layer.subscription


import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import ru.workout24.R
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_subscription_pager.*

/**
 * Вьюпейджер спрезентацией подписок
 */

class SubscriptionPagerFragment : BaseFragment(R.layout.fragment_subscription_pager) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*appBarLayout.setOnBackClick {
            controller.popBackStack()
        }*/

        close.setOnClickListener { controller.popBackStack() }

        tabDots.setupWithViewPager(viewPager)

        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {

            override fun getCount(): Int {
                return 4
            }

            override fun getItem(position: Int): Fragment {
                return SubscriptionPreviewFragment.newInstance(position)
            }
        }

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                when (position) {
                    3 -> {
                        btn_go_to_subscription.isVisible = false
                    }
                    else -> {
                        btn_go_to_subscription.isVisible = true
                    }
                }
            }

        })

        btn_go_to_subscription.setOnClickListener {
            viewPager.currentItem = 3
        }

        close.setOnClickListener {
            controller.popBackStack()
        }
    }

}
