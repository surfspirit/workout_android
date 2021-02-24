package ru.workout24.ui.auth_layer.register_login


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager

import ru.workout24.R
import ru.workout24.ui.auth_layer.register_login.tabs.LoginFragment
import ru.workout24.ui.auth_layer.register_login.tabs.RegisterFragment
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_register_login.*
import org.jetbrains.anko.imageResource

/**
 * Экран с вьюпейджером входа либорегистрации
 */

class RegisterLoginFragment : BaseFragment(R.layout.fragment_register_login) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = RegisterLoginAdapter(childFragmentManager)
        vp_reglog_pager.adapter = adapter
        when(arguments?.getInt(POS_KEY, 0)) {
            0 -> {
                imageView8.imageResource = R.drawable.register_wallpaper
                vp_reglog_pager.currentItem = 0
            }
            1 -> {
                imageView8.imageResource = R.drawable.login_img
                vp_reglog_pager.currentItem = 1
            }
        }
        vp_reglog_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.windowToken, 0)
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                when(position){
                    0 -> {
                     //   view?.backgroundDrawable = activity?.getDrawable(R.drawable.register_wallpaper)
                        imageView8.imageResource = R.drawable.register_wallpaper
                    }
                    1 -> {
                       // view?.backgroundDrawable = activity?.getDrawable(R.drawable.enter_wallpaper)
                        imageView8.imageResource = R.drawable.login_img
                    }
                }
            }

        })
        tl_reglog_tab.setupWithViewPager(vp_reglog_pager)

        LoginFieldsAutoFill.needScrollToLoginScreen().observe(this, Observer {
            vp_reglog_pager.currentItem = 1
        })
    }

    companion object {
        val POS_KEY = "pos"

        fun getBundle(pos: Int): Bundle {
            val args = Bundle()
            args.putInt(POS_KEY, pos)
            return args
        }
    }

    inner class RegisterLoginAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
        override fun getCount(): Int  = 2

        override fun getItem(i: Int): Fragment {
            return when(i){
                0 -> {
                    RegisterFragment.newInstance()
                }
                1 -> {
                    LoginFragment.newInstance()
                }
                else -> RegisterFragment.newInstance()
            }
        }

        override fun getPageTitle(i: Int): CharSequence {
            return when(i){
                0 -> "Регистрация"
                1 -> "Вход"
                else -> "Регистрация"
            }
        }
    }
}


