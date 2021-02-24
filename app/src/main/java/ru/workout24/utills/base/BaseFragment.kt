package ru.workout24.utills.base

import android.content.Context
import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ru.workout24.R
import ru.workout24.network.ResourceProvider
import ru.workout24.utills.Preferences
import ru.workout24.utills.hide
import ru.workout24.utills.show
import com.bumptech.glide.RequestManager
import dagger.android.support.AndroidSupportInjection
import io.reactivex.disposables.CompositeDisposable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.find
import javax.inject.Inject

abstract class BaseFragment(val layoutId: Int = 0, val enableDrawer: Boolean? = false) : Fragment(),
    AnkoLogger, DialogInterface.OnDismissListener {
    private var pendingIsAlertShown = false
    private var progressView: ProgressBar? = null

    private var refreshNeeded: Boolean = false

    protected val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    fun clearSubscriptions() {
        compositeDisposable.clear()
    }

    lateinit var controller: NavController
        get

    @Inject
    lateinit var pref: Preferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var resourceFactory: ResourceProvider

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)

        super.onAttach(context)

        controller = NavHostFragment.findNavController(this)

        //progressView = activity?.find(R.id.progress)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (enableDrawer != true)
            activity?.findViewById<DrawerLayout>(R.id.drawerLayout)?.setDrawerLockMode(
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED,
                Gravity.LEFT
            );

        return inflater.inflate(layoutId, container, false)
    }

    override fun onResume() {
        if (refreshNeeded) refreshFragment()
        super.onResume()
    }

    override fun onPause() {
        clearSubscriptions()
        super.onPause()
    }

    override fun onDetach() {
        clearSubscriptions()
        super.onDetach()
    }

    fun showProgress() {
        progressView?.visibility = View.VISIBLE
    }

    fun hideProgress() {
        progressView?.visibility = View.GONE
    }

    fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(activity?.currentFocus?.windowToken, 0);
    }

    fun showProgressBar(show: Boolean = true) {
        if (show)
            activity?.findViewById<ProgressBar>(R.id.progress)?.show()
        else
            activity?.findViewById<ProgressBar>(R.id.progress)?.hide()
    }

    fun hideProgressBar() = showProgressBar(false)

    @Suppress("UNCHECKED_CAST")
    fun <Y> attachViewModel(
        viewModelClass: Class<out BaseViewModel>,
        fragmentContext: Boolean = true,
        onErrorCallback: (
            uid: Int, message: String
        ) -> Unit = { _, _ -> }
    ): Y {

        /**
         * По-умолчанию контекст вью-модел -- это активити
         */

        val viewModel = if (fragmentContext) {
            ViewModelProviders.of(this, viewModelFactory)
                .get(viewModelClass)
        } else {
            ViewModelProviders.of(activity!!, viewModelFactory)
                .get(viewModelClass)
        }

        viewModel.error.observe(this, Observer {
            it?.let { errorMessage ->
                onErrorCallback(it.uid ?: 0, it.message ?: "")
                viewModel.error.value = null
            }
        })

        viewModel.loading.observe(this, Observer {
            it?.let {
                if (it) showProgress() else hideProgress()
            }
        })

        viewModel.closeWindow.observe(this, Observer {
            it?.let {
                if (!controller.popBackStack()) activity!!.finish()
            }
        })

        return viewModel as Y
    }

    @Suppress("UNCHECKED_CAST")
    fun <Y : BaseAdapter> attachAdapter(adapter: Y): Y {
        adapter.glide = glide
        return adapter
    }

    fun refreshFragment() {
        refreshNeeded = try {
            activity?.supportFragmentManager?.beginTransaction()?.detach(this)?.attach(this)
                ?.commit()
            error { "$loggerTag was refreshed " }
            false
        } catch (e: IllegalStateException) {
            error { "$loggerTag refresh exception" }
            true
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
        alert.setOnDismissListener(this)
        return alert
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
        alert.setOnDismissListener(this)

        return alert
    }

    fun showProgressAlert(
        context: Context,
        text: String,
        dimissable: Boolean = false,
        onDismissListener: () -> Unit = {}
    ): AlertDialog {
        val alert = AlertDialog.Builder(context).create()


        val view = View.inflate(context, R.layout.custom_alert_dialog_progress, null)

        alert.setView(view)
        alert?.window?.setBackgroundDrawable(context.getDrawable(R.drawable.custom_alert_dialog_bg))

        val message = view.find<TextView>(R.id.txt_message)
        message.text = text

        if (!dimissable) {
            alert.setCancelable(false)
            alert.setCanceledOnTouchOutside(false)
        } else {
            alert.setOnDismissListener { onDismissListener() }
        }
        alert.setOnDismissListener(this)

        return alert
    }

    override fun onDismiss(dialog: DialogInterface?) {
        pendingIsAlertShown = false
    }

    fun AlertDialog.showIfNeeded() {
        if (!pendingIsAlertShown) {
            pendingIsAlertShown = true
            show()
        }
    }

    fun getWindowSize(): WindowSize {

        val display = activity!!.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        /*width height*/
        return WindowSize(size.x, size.y)
    }


    inner class WindowSize(
        val width: Int,
        val height: Int
    )

}