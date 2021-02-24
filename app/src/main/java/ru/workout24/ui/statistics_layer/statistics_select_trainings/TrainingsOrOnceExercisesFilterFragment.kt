package ru.workout24.ui.statistics_layer.statistics_select_trainings


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator

import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.room.AppDatabase
import ru.workout24.ui.statistics_layer.once_exercises_trainings.SelectOnceExerciseOrTrainingsFragment
import ru.workout24.ui.trainings.pager.adapters.CategoryOffsetDecorator
import ru.workout24.ui.trainings.pager.adapters.MultiCheckCategoriesAdapter
import ru.workout24.ui.trainings.pager.adapters.MultiCheckCategory
import ru.workout24.ui.trainings.pager.pojos.Criteria
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_trainings_or_once_exercises_filter.*
import javax.inject.Inject

/**
 * Фрагмент для экранов 7 и 8 во вью-пейджере экрана [StatisticsTrainingsFilterFragment]
 * @see https://docs.google.com/document/d/19YAzjzHIWNR-GghihyCXdi7S6p09m72D/edit#heading=h.1g7p69mvimf3
 */
class TrainingsOrOnceExercisesFilterFragment :
    BaseFragment(R.layout.fragment_trainings_or_once_exercises_filter) {

    @Inject
    lateinit var api: Api
    @Inject
    lateinit var db: AppDatabase
    @Inject
    lateinit var resourceProvider: ResourceProvider

    //получаем тип открываемого фрагмента из аргументов
    private val fragmentType: FRAGMENT_TYPE get() = arguments!!.getSerializable(TYPE_KEY) as FRAGMENT_TYPE

    private val viewModel by lazy {
        ViewModelProviders.of(
            this,
            VMTrainingsOrOnceExercisesFilterFactory(api, db, resourceProvider, fragmentType)
        ).get(VMTrainingsOrOnceExercisesFilter::class.java)
    }
    private var adapter: MultiCheckCategoriesAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layout_watch_all.setOnClickListener {
            when (fragmentType) {
                FRAGMENT_TYPE.TRAININGS -> controller.navigate(
                    R.id.action_statisticsTrainingsFilterFragment_to_selectOnceExerciseFragment,
                    SelectOnceExerciseOrTrainingsFragment.getFilterBundle(
                        SelectOnceExerciseOrTrainingsFragment.Companion.OpenType.ONCE_TRAININGS
                    )
                )
                FRAGMENT_TYPE.ONCE_EXERCISES -> controller.navigate(
                    R.id.action_statisticsTrainingsFilterFragment_to_selectOnceExerciseFragment,
                    SelectOnceExerciseOrTrainingsFragment.getFilterBundle(
                        SelectOnceExerciseOrTrainingsFragment.Companion.OpenType.ONCE_EXERCISES
                    )
                )
            }
        }
        btn_apply.setOnClickListener {
            when (fragmentType) {
                FRAGMENT_TYPE.TRAININGS -> controller.navigate(
                    R.id.action_statisticsTrainingsFilterFragment_to_selectOnceExerciseFragment,
                    SelectOnceExerciseOrTrainingsFragment.getFilterBundle(
                        SelectOnceExerciseOrTrainingsFragment.Companion.OpenType.ONCE_TRAININGS,
                        viewModel.getFilterItems()
                    )
                )
                FRAGMENT_TYPE.ONCE_EXERCISES -> controller.navigate(
                    R.id.action_statisticsTrainingsFilterFragment_to_selectOnceExerciseFragment,
                    SelectOnceExerciseOrTrainingsFragment.getFilterBundle(
                        SelectOnceExerciseOrTrainingsFragment.Companion.OpenType.ONCE_EXERCISES,
                        viewModel.getFilterItems()
                    )
                )
            }
        }
        btn_reset.setOnClickListener {
            viewModel.resetFilterItems()
            adapter!!.resetAll()
        }
        // добавляем отступ снизу через декоратор
        recycler_view.addItemDecoration(CategoryOffsetDecorator(resources.getDimensionPixelSize(R.dimen.filter_bottom_margin)))
        viewModel.getAdapterData().observe(this, androidx.lifecycle.Observer {
            adapter = MultiCheckCategoriesAdapter(it)
            recycler_view.adapter = adapter
            val animator = recycler_view.itemAnimator
            if (animator is DefaultItemAnimator) {
                animator.supportsChangeAnimations = false
            }
            adapter!!.setChildClickListener { v, checked, group, childIndex ->
                val field = (group as MultiCheckCategory).serverName
                val value = (group.items[childIndex] as Criteria).value
                if (checked) {
                    viewModel.setFilterItem(field, value)
                } else {
                    viewModel.deleteFilterItem(field, value)
                }

            }
        })
        viewModel.errors.observeMessage(this) {
            it?.let { showInfoAlert(requireContext(), "", it, "Ок", {}).showIfNeeded() }
        }
        viewModel.getIsAcceptFilterEnable().observe(this, Observer { enable ->
            btn_apply.isEnable = enable
            btn_reset.visibility = if (enable) View.VISIBLE else View.INVISIBLE
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        adapter?.onSaveInstanceState(outState)
    }

    companion object {
        private val TYPE_KEY = "TYPE_KEY"

        fun newInstance(type: FRAGMENT_TYPE): TrainingsOrOnceExercisesFilterFragment {
            val fragment = TrainingsOrOnceExercisesFilterFragment()
            val args = Bundle()
            args.putSerializable(TYPE_KEY, type)
            fragment.arguments = args
            return fragment
        }
    }

    enum class FRAGMENT_TYPE {
        TRAININGS, ONCE_EXERCISES, TRAINING_SET
    }
}
