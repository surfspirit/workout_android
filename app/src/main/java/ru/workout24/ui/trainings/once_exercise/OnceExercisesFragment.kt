package ru.workout24.ui.trainings.once_exercise


import android.content.res.Resources
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import ru.workout24.R
import ru.workout24.utills.GO_TO_TRAINING
import ru.workout24.utills.base.BaseFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_once_exercises.*
import ru.workout24.ui.statistics_layer.once_exercises_trainings.adapter.OnceExercisesAdapter
import ru.workout24.ui.statistics_layer.once_exercises_trainings.data.OnceExercisesViewModel
import ru.workout24.utills.base.typed_holder_adapter.AbstractTypeViewModel


class OnceExercisesFragment : BaseFragment(R.layout.fragment_once_exercises) {

    private val filters get() = arguments?.getSerializable(FILTERS_KEY) as? HashMap<String, String>

    private val adapter by lazy {
        OnceExercisesAdapter()
    }
    private val viewModel: VMOnceExercisesGlobal by lazy {
        attachViewModel<VMOnceExercisesGlobal>(
            VMOnceExercisesGlobal::class.java, false
        )
    }
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayoutSub.setOnBackClick {
            //hideProgressBar()
            hideKeyboard()
            controller.popBackStack()
        }
        appBarLayout.setOnBackClick {
            //hideProgressBar()
            hideKeyboard()
            controller.popBackStack()
        }
        txt_sub_header.setText(
            Html.fromHtml(resources.getString(R.string.sub_toolbar_header)),
            TextView.BufferType.SPANNABLE
        )
        sub_button.setOnClickListener {
            hideProgressBar()
            hideKeyboard()
            controller.navigate(R.id.action_global_globalChooseSubscriptionFragment)
        }
        adapter.selectItems = false
        adapter.setItemSelectedCallback { exercise ->
            hideProgressBar()
            if ((exercise as OnceExercisesViewModel).item?.available == true) {
                val b = Bundle()
                b.putString("exercise", Gson().toJson((exercise as OnceExercisesViewModel).item.toExerciseTrainingProgram()))
                b.putSerializable("from", GO_TO_TRAINING.FROM_SINGLE_EX)
                hideKeyboard()
                controller.navigate(R.id.exerciseDescriptionFragment, b)
            }

        }
        rv_once_exercise_list.adapter = adapter
        viewModel.filters = filters
        viewModel.getExercises()
        viewModel.liveData.observe(this, Observer {
            it?.let {
                resourceFactory.userResource.load()
            }
        })
        resourceFactory.userResource.onChange(this, { user ->
            viewModel.liveData.value?.let { list ->
                adapter.setData(list.map { OnceExercisesViewModel(it) } as ArrayList<AbstractTypeViewModel>)
            }
        }, {

        })
        searchview.onTextChanged { text ->
            adapter.filter(text) { query, element ->
                (element as OnceExercisesViewModel).item.name?.contains(query, true) ?: false
            }
        }
    }

    companion object {
        val FILTERS_KEY = "FILTERS_KEY"
        fun getBundle(filters: HashMap<String, String>) = Bundle().apply {
            putSerializable(FILTERS_KEY, filters)
        }
    }
}
