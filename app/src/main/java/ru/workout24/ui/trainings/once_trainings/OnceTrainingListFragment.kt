package ru.workout24.ui.trainings.once_trainings

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.gson.Gson

import ru.workout24.R
import ru.workout24.utills.*
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_once_training_list.*
import org.jetbrains.anko.backgroundColor
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.room.AppDatabase
import ru.workout24.ui.statistics_layer.once_exercises_trainings.adapter.OnceExercisesAdapter
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.OnceTrainingsViewModel
import ru.workout24.ui.statistics_layer.statistics_select_trainings.TrainingsOrOnceExercisesFilterFragment
import ru.workout24.ui.statistics_layer.statistics_select_trainings.VMTrainingsOrOnceExercisesFilter
import ru.workout24.ui.statistics_layer.statistics_select_trainings.VMTrainingsOrOnceExercisesFilterFactory
import javax.inject.Inject


class OnceTrainingListFragment : BaseFragment(R.layout.fragment_once_training_list) {

    @Inject
    lateinit var api: Api
    @Inject
    lateinit var db: AppDatabase
    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val viewModel: VMTrainings by lazy {
        attachViewModel<VMTrainings>(
            VMTrainings::class.java
        )
    }

    private val filterViewModel by lazy {
        ViewModelProviders.of(
            this,
            VMTrainingsOrOnceExercisesFilterFactory(
                api,
                db,
                resourceProvider,
                TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE.TRAININGS
            )
        ).get(VMTrainingsOrOnceExercisesFilter::class.java)
    }

    private val adapter by lazy {
        OnceExercisesAdapter(showSelection = false)
    }

    private val scrollListener = ViewTreeObserver.OnScrollChangedListener {
        //TODO: сделать нормальное удаление листенера
        if (scroll_cont != null && scroll_cont.scrollY > 0) {
            appBar?.backgroundColor =
                ContextCompat.getColor(requireContext(), R.color.black)
        } else {
            appBar?.backgroundColor = Color.TRANSPARENT
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBar.setOnBackClick {
            controller.popBackStack()
        }
        txt_sub_header.setText(
            Html.fromHtml(resources.getString(R.string.sub_toolbar_header)),
            TextView.BufferType.SPANNABLE
        )
        sub_button.setOnClickListener {
            hideProgressBar()
            controller.navigate(R.id.globalChooseSubscriptionFragment)
        }
        filterVIew.apply {
            setFilterLiveData(this@OnceTrainingListFragment, filterViewModel.getAdapterData())
            setApplyClick {
                viewModel.loadTrainings(filterViewModel.getFilterItems())
            }
            setResetClick {
                filterViewModel.resetFilterItems()
                viewModel.loadTrainings()
            }
            setFilterItem { field, value -> filterViewModel.setFilterItem(field, value) }
            deleteFilterItem { field, value -> filterViewModel.deleteFilterItem(field, value) }
        }
        viewModel.trainings.observe(this, Observer {
            resourceProvider.userResource.loadIfNeeded()
        })
        resourceProvider.userResource.load()
        resourceProvider.userResource.onChange(this, { user ->
            if (user.have_subscription) {
                subContainer.hide()
                filterVIew.setPadding(0, appBar.height, 0, 0)
                appBar.backgroundColor = ContextCompat.getColor(requireContext(), R.color.black)
            } else {
                subContainer.show()
                scroll_cont.viewTreeObserver.addOnScrollChangedListener(scrollListener)
            }
            viewModel.trainings.value?.filter {
                it.item.trainingLevel == user.trainingLevel &&
                        (it.item.gender == null || it.item.gender == GENDER.NONE.name || it.item.gender == user.gender || user.gender == null || user.gender == GENDER.NONE.name)
            }?.toArrayList()?.let {
                adapter.setData(it)
                adapter.notifyDataSetChanged()
            }


        }, {
            if (!it.startsWith("Query returned empty result"))
                showInfoAlert(
                    context!!,
                    "Ошибка",
                    "Произошла ошибка выполнения запроса",
                    "ОК",
                    {}).show()
        })
        viewModel.loadTrainings()
        rv_once_training_list.adapter = adapter
        adapter.setItemSelectedCallback { training ->
            training as OnceTrainingsViewModel
            hideProgressBar()
            val b = Bundle()
            b.putSerializable("from", GO_TO_TRAINING.FROM_ONCE_TRAININGS)
            training.item.id.let { b.putString("id", it) }
//            b.putString("trainingData", Gson().toJson(training.item))
            controller.navigate(R.id.action_onceTrainingListFragment_to_aboutTrainingFragment, b)
        }
    }
}
