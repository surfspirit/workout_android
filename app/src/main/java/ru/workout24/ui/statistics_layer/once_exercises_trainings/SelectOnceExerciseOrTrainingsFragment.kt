package ru.workout24.ui.statistics_layer.once_exercises_trainings


import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.room.AppDatabase
import ru.workout24.ui.statistics_layer.once_exercises_trainings.adapter.OnceExercisesAdapter
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.OnceExercisesViewModel
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.OnceTrainingsViewModel
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel
import kotlinx.android.synthetic.main.fragment_select_once_exercise.*
import ru.workout24.utills.GENDER
import ru.workout24.utills.toArrayList
import javax.inject.Inject


class SelectOnceExerciseOrTrainingsFragment : BaseFragment(R.layout.fragment_select_once_exercise) {
    @Inject
    lateinit var api: Api
    @Inject
    lateinit var db: AppDatabase
    @Inject
    lateinit var res: ResourceProvider

    private val type get() = arguments!!.getSerializable(TYPE_KEY) as OpenType
    private val filters get() = arguments?.getSerializable(FILTERS_KEY) as? HashMap<String, String>
    private val adapter by lazy {
        OnceExercisesAdapter()
    }
    private val viewModel: VMSelectOnceExercises by lazy {
        ViewModelProviders.of(this, ViewModelWithFiltersFactory(api, db, res, type, filters))
            .get(VMSelectOnceExercises::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.setTitleText(type.title)
        appBarLayout.setOnBackClick {
            hideKeyboard()
            controller.popBackStack()
        }
        iv_done.setOnClickListener {
            viewModel.done()
            controller.popBackStack(R.id.statisticsTrainingsFilterFragment, true)
        }
        viewModel.selectedItem.observe(this, Observer { ex ->
            if (ex != null) {
                iv_done.setImageResource(R.drawable.ic_done)
            } else {
                iv_done.setImageResource(R.drawable.ic_done_gray)
            }
        })
        searchview.onTextChanged { text ->
            adapter.filter(text) { query, element ->
                when(type) {
                    OpenType.ONCE_EXERCISES -> {
                        element as OnceExercisesViewModel
                        element.item.name?.contains(query, true) ?: false
                    }
                    OpenType.ONCE_TRAININGS -> {
                        element as OnceTrainingsViewModel
                        element.item.name?.contains(query, true) ?: false
                    }
                }
            }
        }
        viewModel.onceExercises.observe(this, Observer {
                resourceFactory.userResource.load()
        })
        viewModel.errors.observeMessage(this) {
            it?.let { showInfoAlert(requireContext(), "", it, "Ок", {}).showIfNeeded() }
        }
        viewModel.load()
        resourceFactory.userResource.onChange(this, { user ->
            val items = viewModel.onceExercises.value
            items?.let { it ->
                var items = it
                when(type) {
                    OpenType.ONCE_TRAININGS -> items = items.filter { (it as OnceTrainingsViewModel).item.trainingLevel == user.trainingLevel &&
                            (it.item.gender==null || it.item.gender== GENDER.NONE.name || it.item.gender==user.gender || user.gender == null || user.gender == GENDER.NONE.name) }.toArrayList()
                }
                adapter.setData(items as ArrayList<AbstractTypeViewModel>)
            }
            adapter.setItemSelectedCallback { item ->
                val avable = when(type) {
                    OpenType.ONCE_EXERCISES -> (item as OnceExercisesViewModel).item.available
                    OpenType.ONCE_TRAININGS -> (item as OnceTrainingsViewModel).item.available
                }
                if (avable == true) {
                    viewModel.selectedItem.postValue(item)
                }
            }
            rv_once_exercise_list.adapter = adapter
        }, {
            showInfoAlert(context!!,"Ошибка","Произошла ошибка выполнения запроса","ОК",{}).show()
        })

    }

    companion object {
        const val TYPE_KEY = "TYPE_KEY"
        const val FILTERS_KEY = "FILTERS_KEY"

        fun getFilterBundle(type: OpenType, filters: HashMap<String, String>? = null): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(TYPE_KEY, type)
            bundle.putSerializable(FILTERS_KEY, filters)
            return bundle
        }

        enum class OpenType(val title: String) {
            ONCE_EXERCISES("Упражнения"), ONCE_TRAININGS("Тренировки")
        }
    }
}
