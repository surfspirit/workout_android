package ru.workout24.ui.trainings

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_trainings.*
import org.jetbrains.anko.support.v4.toast
import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.network.TimezoneBody
import ru.workout24.ui.trainings.pager.OnceExercisesFilterFragment
import ru.workout24.ui.trainings.pager.TrainingCategoriesFragment
import ru.workout24.utills.base.BaseFragment
import java.util.*
import javax.inject.Inject


class TrainingsFragment : BaseFragment(R.layout.fragment_trainings, true) {
    @Inject
    lateinit var resProvider: ResourceProvider
    @Inject
    lateinit var api: Api

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = TrainingsAdapter(childFragmentManager)
        vp_trainings_pager.adapter = adapter
        val drawer = activity?.findViewById<DrawerLayout>(R.id.drawerLayout)
        drawer?.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT)
        tl_trainings_tab.setupWithViewPager(vp_trainings_pager)
        iv_menu.setOnClickListener {
            drawer?.openDrawer(GravityCompat.START)
        }
        val spannable = SpannableString("WORKOUT GENERATION")
        spannable.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.coral)),
            0, 7,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        textView18.text = spannable
        sendTimezoneIfNeeded()

        resProvider.userResource.loadIfNeeded()
    }

    inner class TrainingsAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(
        fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {

        override fun getCount(): Int = 2

        override fun getItem(i: Int): Fragment {
            return when (i) {
                0 -> {
                    TrainingCategoriesFragment.newInstance()
                }
                1 -> {
                    OnceExercisesFilterFragment.newInstance()
                }
                else -> TrainingCategoriesFragment.newInstance()
            }
        }

        override fun getPageTitle(i: Int): CharSequence {
            return when (i) {
                0 -> "Тренировки"
                1 -> "Упражнения"
                else -> "Тренировки"
            }
        }
    }

    private fun sendTimezoneIfNeeded() {
        val timeZone = TimeZone.getDefault().id
        if (pref.timeZone.isNullOrEmpty() && pref.timeZone != timeZone) {
            pref.timeZone = timeZone
            compositeDisposable.add(
                api.changeTimezone(TimezoneBody(timeZone)).subscribeOn(Schedulers.io()).observeOn(
                    AndroidSchedulers.mainThread()
                ).subscribe({
                    it
                }, {
                    toast("Не удалось обновить часвой пояс")
                })
            )
        }
    }
}
