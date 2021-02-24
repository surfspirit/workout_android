package ru.workout24.ui.trainings.training_programs


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import ru.workout24.R
import ru.workout24.ui.trainings.training_programs.pojos.TrainingProgramShortDescription
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_training_programs.*
import org.jetbrains.anko.find
import ru.workout24.network.Api
import ru.workout24.network.ResourceProvider
import ru.workout24.room.AppDatabase
import ru.workout24.ui.statistics_layer.statistics_select_trainings.TrainingsOrOnceExercisesFilterFragment
import ru.workout24.ui.statistics_layer.statistics_select_trainings.VMTrainingsOrOnceExercisesFilter
import ru.workout24.ui.statistics_layer.statistics_select_trainings.VMTrainingsOrOnceExercisesFilterFactory
import ru.workout24.utills.*
import javax.inject.Inject


class TrainingProgramsFragment : BaseFragment(R.layout.fragment_training_programs) {
    @Inject
    lateinit var api: Api
    @Inject
    lateinit var db: AppDatabase
    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val adapter: TrainingProgramsAdapter by lazy {
        attachAdapter(TrainingProgramsAdapter(requireContext()))
    }
    private val viewModel: VMTrainingsShort by lazy {
        attachViewModel<VMTrainingsShort>(
            VMTrainingsShort::class.java
        )
    }

    private val filterViewModel by lazy {
        ViewModelProviders.of(
            this,
            VMTrainingsOrOnceExercisesFilterFactory(
                api,
                db,
                resourceProvider,
                TrainingsOrOnceExercisesFilterFragment.FRAGMENT_TYPE.TRAINING_SET
            )
        ).get(VMTrainingsOrOnceExercisesFilter::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarLayout.setOnBackClick {
            hideProgressBar()
            controller.popBackStack()
        }
        adapter.setItemSelectedCallback { pos ->
            hideProgressBar()
            val b = Bundle()
            b.putString("id", (adapter.data as ArrayList<TrainingProgramShortDescription>)[pos!!].id)
            controller.navigate(R.id.trainingProgramFragment, b)
        }
        rv_training_programs.adapter = adapter
        viewModel.programs.observe(viewLifecycleOwner, Observer {
            it?.let { programs ->
                adapter.data = programs.toArrayList()
                adapter.notifyDataSetChanged()
            }
        })
        program_filter.apply {
            setFilterLiveData(this@TrainingProgramsFragment.viewLifecycleOwner, filterViewModel.getAdapterData())
            setApplyClick {
                viewModel.loadPrograms(filterViewModel.getFilterItems())
            }
            setResetClick {
                filterViewModel.resetFilterItems()
                viewModel.loadPrograms()
            }
            setFilterItem { field, value -> filterViewModel.setFilterItem(field, value) }
            deleteFilterItem {
                    field, value -> filterViewModel.deleteFilterItem(field, value)
            }
        }
    }

    class TrainingProgramsAdapter(val context: Context) : BaseAdapter(R.layout.item_training_program) {
        private var itemSelectedCallback: (pos: Int?) -> Unit = {}

        init {
            onBind { holder, pos ->
                val txtStatus = holder.find<TextView>(R.id.txt_program_status)
                val txtSkillLevel = holder.find<TextView>(R.id.txt_skill_level)
                val txtGoal = holder.find<TextView>(R.id.txt_goal)
                val txtProgramName = holder.find<TextView>(R.id.txt_program_name)
                val txtDuration = holder.find<TextView>(R.id.txt_duration)
                val ivTrainingProgramImage = holder.find<ImageView>(R.id.iv_training_program_image)
                val item = (data[pos]) as TrainingProgramShortDescription

                txtProgramName.text = item.name
                txtGoal.text = goals[item.goals]
                txtSkillLevel.text = levels[item.trainingLevel]
                item.weeks_count?.let {
                    txtDuration.text = ru.workout24.utills.getFormattedStringWeek(it)
                }
                glide?.load(item.previewUrl)?.into(ivTrainingProgramImage)

                if (item.available == true) {
                    txtStatus.text = "Доступно"
                } else txtStatus.text = "По подписке"


                holder.setOnClickListener {
                    itemSelectedCallback(pos)
                }
            }
        }

        fun setItemSelectedCallback(itemSelectedCallback: (pos: Int?) -> Unit) {
            this.itemSelectedCallback = itemSelectedCallback
        }
    }
}
