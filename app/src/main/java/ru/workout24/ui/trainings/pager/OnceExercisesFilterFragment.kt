package ru.workout24.ui.trainings.pager


import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import ru.workout24.R
import ru.workout24.ui.trainings.once_exercise.VMOnceExercisesGlobal
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_once_exercises_filters.*
import kotlinx.android.synthetic.main.fragment_once_exercises_filters.layout_watch_all
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.room.AppDatabase
import ru.workout24.ui.statistics_layer.statistics_select_trainings.TrainingsOrOnceExercisesFilterFragment
import ru.workout24.ui.statistics_layer.statistics_select_trainings.VMTrainingsOrOnceExercisesFilter
import ru.workout24.ui.statistics_layer.statistics_select_trainings.VMTrainingsOrOnceExercisesFilterFactory
import ru.workout24.ui.trainings.once_exercise.OnceExercisesFragment
import javax.inject.Inject


/**
 * Фрагмент с разовыми упражнениями
 *
 */
class OnceExercisesFilterFragment : BaseFragment(R.layout.fragment_once_exercises_filters, true) {
    @Inject
    lateinit var api: Api
    @Inject
    lateinit var db: AppDatabase
    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val viewModel: VMOnceExercisesGlobal by lazy {
        attachViewModel<VMOnceExercisesGlobal>(
            VMOnceExercisesGlobal::class.java, false
        )
    }

    private val filterViewModel by lazy {
        ViewModelProviders.of(
            this,
            VMTrainingsOrOnceExercisesFilterFactory(
                api,
                db,
                resourceProvider,
                TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE.ONCE_EXERCISES
            )
        ).get(VMTrainingsOrOnceExercisesFilter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layout_watch_all.setOnClickListener {
            controller.navigate(R.id.action_global_onceExercisesFragment)
        }

        filterView.setFilterLiveData(this, filterViewModel.getAdapterData())
        filterView.setApplyClick {
            controller.navigate(R.id.action_global_onceExercisesFragment, OnceExercisesFragment.getBundle(filterViewModel.getFilterItems()))
        }
        filterView.setResetClick {
            filterViewModel.resetFilterItems()
        }
        filterView.setFilterItem { field, value -> filterViewModel.setFilterItem(field, value) }
        filterView.deleteFilterItem { field, value -> filterViewModel.deleteFilterItem(field, value) }
        filterView.setAcceptFilterLiveData(filterViewModel.getIsAcceptFilterEnable(), this)
    }

    companion object {
        fun newInstance(): OnceExercisesFilterFragment {
            val fragment = OnceExercisesFilterFragment()

            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }
}
