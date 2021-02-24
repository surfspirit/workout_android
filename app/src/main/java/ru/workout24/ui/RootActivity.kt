package ru.workout24.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import ru.workout24.R
import ru.workout24.utills.base.BaseActivity
import ru.workout24.utills.hide
import kotlinx.android.synthetic.main.activity_main.*
import ru.workout24.push.DeepLinkStore
import ru.workout24.features.SocialLoginFeature
import ru.workout24.ui.auth_layer.register_login.RegisterLoginFragment
import java.lang.IllegalStateException
import javax.inject.Inject


class RootActivity : BaseActivity() {
    private var activityWasCreated = false

    @Inject
    lateinit var socialLoginFeature: SocialLoginFeature

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        activityWasCreated = true

        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        setContentView(R.layout.activity_main)
        initController(R.id.root_host_fragment)

        when {
            pref.authToken.isNullOrEmpty() -> controller?.navigate(R.id.action_enterFragment_to_auth_layer, RegisterLoginFragment.getBundle(1))
            pref.notify_passed != true -> controller?.navigate(R.id.action_enterFragment_to_applyNotifFragment)
            pref.gender_date_selected != true -> controller?.navigate(R.id.action_enterFragment_to_genderChoiceFragment)
            pref.anket_passed != true -> controller?.navigate(R.id.action_enterFragment_to_anket_layer)
            pref.test_passed != true -> controller?.navigate(R.id.test_layer)
            !pref.authToken.isNullOrEmpty() -> controller?.navigate(R.id.action_enterFragment_to_menu_layer).also {
                if (DeepLinkStore.hasData) {
                    controller?.navigate(DeepLinkStore.getObject().value)
                }
            }
        }
//        val fingerPrint = VKUtils.getCertificateFingerprint(this, this.packageName)
//        showInfoAlert(this, "", fingerPrint?.get(0) ?: "","", {}).show()
    }

    private fun setWindowFlag(activity: Activity, bits: Int, state: Boolean) {
        val win = activity.window
        val winParams = win.attributes

        if (state) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
    }

    override fun initController(id: Int) {
        super.initController(id)
        controller?.let {
            setupActionBarWithNavController(it, drawerLayout)

            // Handle nav drawer item clicks
            navigationView.setNavigationItemSelectedListener { menuItem ->
                menuItem.isChecked = true
                if (menuItem.itemId == R.id.action_global_test_layer3) {
                    it.navigate(R.id.action_global_test_layer3)
                }
                drawerLayout.closeDrawers()
                true
            }

            // Tie nav graph to items in nav drawer
            NavigationUI.setupWithNavController(navigationView, it)

            // показывать опцию для теста пушей в дарвере под дебагом
//            if (BuildConfig.DEBUG) {
//                navigationView.find<LinearLayout>(R.id.debug_options).apply {
//                    show()
//                    find<Button>(R.id.push_debug).setOnClickListener {
//                        startActivity(Intent(this@BaseActivity, PushDebugActivity::class.java))
//                    }
//                }
//            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        socialLoginFeature.handleVkResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        DeepLinkStore.createPushNavigation(intent)
        if (activityWasCreated && DeepLinkStore.hasData) {
            controller?.navigate((DeepLinkStore.getObject().value))
        }
    }

    override fun onBackPressed() {
        progress.hide()
        super.onBackPressed()
    }
}