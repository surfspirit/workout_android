package ru.workout24.ui.helpful_info


import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import ru.workout24.R
import ru.workout24.ui.helpful_info.adapter.ViewPagerAdapter
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.hide
import ru.workout24.utills.show
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_helpful_info.*

/**
 * A simple [Fragment] subclass.
 */
class HelpfulInfoFragment : BaseFragment(R.layout.fragment_helpful_info) {
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        resourceFactory.userResource.load()
        resourceFactory.userResource.onChange(this, {user ->
            if (!user.have_subscription) {
                (htab_collapse_toolbar.layoutParams as AppBarLayout.LayoutParams).height = 248.px
                startUsing.show();
            } else {
                (htab_collapse_toolbar.layoutParams as AppBarLayout.LayoutParams).height = 192.px
                startUsing.hide();
            }
        }, {_ ->
            showInfoAlert(context!!, "Ошибка при выполнение запроса", "Произошла ошибка при выполнении запроса к серверу", "Ок", {});
        })
        super.onViewCreated(view, savedInstanceState)
        val pagerAdapter = ViewPagerAdapter(childFragmentManager)
        vp_articles.adapter = pagerAdapter
        tl_articles.setupWithViewPager(vp_articles)
        val drawer = activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
        drawer?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, GravityCompat.START)
        iv_menu.setOnClickListener {
            drawer?.openDrawer(GravityCompat.START)
        }
        startUsing.setOnClickListener {
            controller.navigate(R.id.globalChooseSubscriptionFragment, bundleOf("show_back_button" to true))
        }
    }
}
