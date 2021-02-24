package ru.workout24.utills.base

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import ru.workout24.R
import ru.workout24.utills.Preferences
import dagger.android.AndroidInjection
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.HasSupportFragmentInjector
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(), HasSupportFragmentInjector,
    NavController.OnDestinationChangedListener,
    AnkoLogger {

    protected val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    fun clearSubscriptions() {
        compositeDisposable.clear()
    }

    var controller: NavController? = null
        get

    @Inject
    lateinit var pref: Preferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    override fun supportFragmentInjector() = dispatchingAndroidInjector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)
    }

    open fun initController(id: Int) {
        setSupportActionBar(toolbar)
        controller = Navigation.findNavController(this, id)
        controller?.addOnDestinationChangedListener(this)
    }

    fun setToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    @Suppress("UNCHECKED_CAST")
    fun <Y> attachViewModel(viewModelClass: Class<out BaseViewModel>): Y {
        val viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(viewModelClass)

        viewModel.closeWindow.observe(this, Observer {
            it?.let {
                finish()
            }
        })

        return viewModel as Y
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        supportActionBar?.title = destination.label
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                controller?.let {
                    if (!it.popBackStack()) onBackPressed()
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        var currentFragment: Fragment? = null
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {


            for (navHostFragment in supportFragmentManager.fragments) {
                if (navHostFragment is NavHostFragment) {
                    currentFragment = navHostFragment.childFragmentManager.fragments[0]
                }
            }

            if (currentFragment != null && currentFragment is OnBackPressedCallback) currentFragment.onBackPressedCallback()

            if (currentFragment != null && currentFragment is OnBackPressedListener) {
                currentFragment.onBackPressed()
            } else {
                controller?.let {
                    if (!it.popBackStack()) super.onBackPressed()
                }
            }
        }
    }

    override fun onDestroy() {
        clearSubscriptions()
        super.onDestroy()
    }

    fun showActionAlert(
        context: Context,
        titleAlert: String,
        text: String,
        okBtnText: String,
        cancelBtnText: String,
        onOkListener: () -> Unit,
        onDismissListener: () -> Unit = {},
        onCancelListener: () -> Unit = {}
    ): AlertDialog {
        val alert = AlertDialog.Builder(context).create()

        val dismiss = {
            onDismissListener()
            alert.dismiss()
        }

        val view = View.inflate(context, R.layout.custom_alert_dialog_action, null)

        alert.setView(view)
        alert?.window?.setBackgroundDrawable(context.getDrawable(R.drawable.custom_alert_dialog_bg))

        val title = view.find<TextView>(R.id.txt_title)
        title.text = titleAlert

        val message = view.find<TextView>(R.id.txt_message)
        message.text = text

        val okBtn = view.find<TextView>(R.id.ok_btn)
        okBtn.text = okBtnText
        okBtn.setOnClickListener {
            dismiss()
            onOkListener()
        }

        val cancelBtn = view.find<TextView>(R.id.cancel_btn)
        cancelBtn.text = cancelBtnText
        cancelBtn.setOnClickListener {
            dismiss()
            onCancelListener()
        }

        return alert
    }
}

fun showInfoAlert(
    context: Context,
    titleAlert: String,
    text: String,
    okBtnText: String, onOkListener: () -> Unit,
    onDismissListener: () -> Unit = {}
): AlertDialog {
    val alert = AlertDialog.Builder(context).create()

    val dismiss = {
        onDismissListener()
        alert.dismiss()
    }

    val view = View.inflate(context, R.layout.custom_alert_dialog_info, null)

    alert.setView(view)
    alert?.window?.setBackgroundDrawable(context.getDrawable(R.drawable.custom_alert_dialog_bg))

    val title = view.find<TextView>(R.id.txt_title)
    title.text = titleAlert

    val message = view.find<TextView>(R.id.txt_message)
    message.text = text

    val okBtn = view.find<TextView>(R.id.ok_btn)
    okBtn.text = okBtnText
    okBtn.setOnClickListener {
        dismiss()
        onOkListener()
    }

    return alert
}

interface OnBackPressedListener {
    fun onBackPressed()
}

interface OnBackPressedCallback {
    fun onBackPressedCallback()
}