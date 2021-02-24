package ru.workout24.ui.statistics_layer.edit_statistics_entry

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.annotations.SerializedName
import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.network.UserStatistic
import ru.workout24.room.AppDatabase
import ru.workout24.ui.lk_layer.edit_profile.data.model.*
import ru.workout24.ui.lk_layer.edit_profile.utils.Dialogs
import ru.workout24.ui.statistics_layer.edit_statistics_entry.adapter.StatisticsEntryAdapter
import ru.workout24.ui.statistics_layer.edit_statistics_entry.adapter.StatisticsEntryListener
import ru.workout24.ui.statistics_layer.statistics.StatisticsDateFragment
import ru.workout24.utills.ISOfromCalendar
import ru.workout24.utills.ImagePicker
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.hide
import kotlinx.android.synthetic.main.fragment_edit_statistics_entry.*
import ru.workout24.utills.SwitchButton
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


class EditStatisticsEntryFragment : BaseFragment(R.layout.fragment_edit_statistics_entry),
    StatisticsEntryListener {
    @Inject
    lateinit var api: Api
    @Inject
    lateinit var db: AppDatabase

    private val fragmentType
        get() = arguments?.getSerializable(FRAGMENT_OPEN_TYPE_KEY) as? FRAGMENT_OPEN_TYPE
            ?: FRAGMENT_OPEN_TYPE.EDIT
    private val statisticType
        get() = arguments!!.getSerializable(STATISTIC_TYPE_KEY) as StatisticsDateFragment.DateItemType
    private val data get() = arguments?.getParcelable<UserStatistic>(DATA_KEY)
    private val popBackStackInclude get() = arguments?.getInt(POP_BACK_STACK, 0)

    private val viewModel: VMEditStatisticsEntry by lazy {
        ViewModelProviders.of(
            this,
            VMEditStatisticsEntryFactory(api, db, data, fragmentType, statisticType)
        )
            .get(VMEditStatisticsEntry::class.java)
    }

    private val adapter by lazy {
        StatisticsEntryAdapter(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rv_statistics_entry.adapter = adapter
        iv_close.setOnClickListener {
            if (popBackStackInclude != null && popBackStackInclude != 0) {
                controller.popBackStack(popBackStackInclude!!, false)
            } else {
                controller.popBackStack()
            }
        }

        when (fragmentType) {
            FRAGMENT_OPEN_TYPE.ADD -> {
                iv_delete.hide()
                btn_save.text = "Готово"
                btn_save.setOnClickListener {
                    viewModel.done()
                }
            }
            FRAGMENT_OPEN_TYPE.EDIT -> {
                iv_delete.setOnClickListener {
                    showActionAlert(
                        context!!,
                        "Удалить статистику",
                        "Вы хотите удалить эту статистику?",
                        "Да",
                        "Нет",
                        onOkListener = {
                            viewModel.delete()
                        },
                        onCancelListener = {}).show()
                }
                btn_save.setOnClickListener {
                    showActionAlert(
                        context!!,
                        "",
                        "Вы хотите сохранить изменения?",
                        "Да",
                        "Нет",
                        onOkListener = {
                            viewModel.save()
                        },
                        onCancelListener = {
//                            if (popBackStackInclude != null && popBackStackInclude != 0) {
//                                controller.popBackStack(popBackStackInclude!!, false)
//                            } else {
//                                controller.popBackStack()
//                            }
                        }).show()
                }
            }
        }
        viewModel.getStatisticsEntryItems()
        viewModel.statisticsItems.observe(this, Observer { data ->
            adapter.setData(data)
        })
        viewModel.imagesUrl.observe(this, Observer { url ->
            adapter.photoUrlAdded()
        })
        viewModel.popBackStackIsSuccess.observe(this) { message ->
            showInfoAlert(requireContext(), "", message, "Ок", {
                if (popBackStackInclude != null && popBackStackInclude != 0) {
                    controller.popBackStack(popBackStackInclude!!, false)
                } else {
                    controller.popBackStack()
                }
            }).showIfNeeded()
        }
        viewModel.errorLiveData.observe(this) { error ->
            error.message?.let { message ->
                showInfoAlert(requireContext(), "", message, "Ok", {}).showIfNeeded()
            }
        }
        viewModel.errorLiveData.observeThrowable(this) { error ->
            error.message?.let { message ->
                showInfoAlert(requireContext(), "", message, "Ok", {}).showIfNeeded()
            }
        }
    }

    override fun onItemClick(data: ProfileTextViewModel, position: Int) {
        when (data) {
            is ProfileSelectEnumViewModel<*> -> Dialogs.showWheelInputDialog(
                requireContext(),
                data
            ) {
                adapter.notifyItemChanged(position)
            }
            is ProfileDateViewModel -> if (data.canModify) {
                val minDate = Calendar.getInstance()
                minDate.add(Calendar.DATE, -30)
                Dialogs.showDatePickerDialog(
                    requireContext(),
                    data,
                    minDate = minDate.timeInMillis
                ) { calendar ->
                    //TODO:убрать Z после того как поправят на сервере
                    viewModel.putField(data.id, "${calendar.ISOfromCalendar()}Z")
                    adapter.notifyItemChanged(position)
                }
            }
            else -> Dialogs.showTextInputDialog(requireContext(), data) {
                viewModel.putField(data.id, data.value)
                adapter.notifyItemChanged(position)
            }
        }
    }

    override fun onSwitchChange(switch: SwitchButton, data: ProfileSwitchViewModel) {
    }

    override fun onNavigateClick(data: ProfileArrowViewModel) {
        controller.navigate(data.navigateTo)
    }

    override fun onPickImage(position: Int) {
        ImagePicker.pickImage(this)
    }

    override fun onDeleteImage(position: Int) {
        adapter.photoUrlRemoved(position)
    }

    override fun onImagesChanged(position: Int, urls: ArrayList<String>) {
        adapter.notifyItemChanged(position)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ImagePicker.validateRequestedPermissions(this, requestCode, permissions, grantResults) {
            //            Toast.makeText(requireContext(), "")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ImagePicker.getPickedImage(requireContext(), requestCode, resultCode, data, viewModel)
    }

    companion object {
        const val FRAGMENT_OPEN_TYPE_KEY = "FRAGMENT_OPEN_TYPE_KEY"
        const val STATISTIC_TYPE_KEY = "STATISTIC_TYPE_KEY"
        const val DATA_KEY = "DATA_KEY"
        const val POP_BACK_STACK = "POP_BACK_STACK"

        fun getAddStatisticsEntryBundle(
            statisticType: StatisticsDateFragment.DateItemType
        ): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(FRAGMENT_OPEN_TYPE_KEY, FRAGMENT_OPEN_TYPE.ADD)
            bundle.putSerializable(STATISTIC_TYPE_KEY, statisticType)
            return bundle
        }

        fun getEditStatisticsEntryBundle(
            data: UserStatistic,
            statisticType: StatisticsDateFragment.DateItemType
        ): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(FRAGMENT_OPEN_TYPE_KEY, FRAGMENT_OPEN_TYPE.EDIT)
            bundle.putSerializable(STATISTIC_TYPE_KEY, statisticType)
            bundle.putParcelable(DATA_KEY, data)
            return bundle
        }

        fun getEditStatisticEntryFromTrainingResult(
            data: UserStatistic,
            statisticType: StatisticsDateFragment.DateItemType, popBackStackInclude: Int = R.id.aboutTrainingFragment
        ): Bundle {
            val bundle = getEditStatisticsEntryBundle(data, statisticType)
            bundle.putInt(POP_BACK_STACK, popBackStackInclude)
            return bundle
        }
    }
}


enum class FRAGMENT_OPEN_TYPE {
    EDIT,
    ADD
}