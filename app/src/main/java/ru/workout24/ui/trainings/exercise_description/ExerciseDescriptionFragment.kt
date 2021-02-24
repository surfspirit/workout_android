package ru.workout24.ui.trainings.exercise_description

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import ru.workout24.R
import ru.workout24.ui.trainings.training_programs.pojos.ExerciseTrainingProgram
import ru.workout24.utills.*
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_exercise_description.*
import org.jetbrains.anko.find
import ru.workout24.ui.exercises_execution.ExercisesExecutionHostFragment

class ExerciseDescriptionFragment : BaseFragment(R.layout.fragment_exercise_description) {
    private lateinit var exercise: ExerciseTrainingProgram

    private val onlyShow get() = arguments?.getBoolean("onlyShow", false) ?: false

    private val adapter: ExerciseAttrs by lazy {
        attachAdapter(ExerciseAttrs(context!!))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.setOnBackClick { controller.popBackStack() }
        exercise = Gson().fromJson(arguments?.getString("exercise"), ExerciseTrainingProgram::class.java)
        playerView.initVimeoPlayer(exercise.videoUrl + "?autoplay=1&amp;title=1&amp;byline=1&amp;portrait=0")

        if (arguments?.getSerializable("from") == GO_TO_TRAINING.FROM_SINGLE_EX) {
            btn_start_or_sub.show()
        } else
            btn_start_or_sub.hide()


        Glide.with(activity?.applicationContext!!).load(exercise.previewUrl).into(iv_preview)
        iv_muscles.hide()
        iv_muscles2.hide()
        exercise.muscle_groups?.let {
            it.forEachIndexed { index, muscleGroup ->
                when (index) {
                    0 -> {
                        iv_muscles.show()
                        glide.load(exercise.muscle_groups?.get(index)?.previewUrl).into(iv_muscles)
                    }
                    1 -> {
                        iv_muscles2.show()
                        glide.load(exercise.muscle_groups?.get(index)?.previewUrl).into(iv_muscles2)
                    }

                }
            }
        }
        glide.load(exercise.muscle_groups?.getOrNull(0)?.previewUrl).into(iv_muscles)
        val list = arrayListOf<SimpleValue>()
        list.add(SimpleValue("Повторов", getCountByRange(exercise.requiredRange?.min,exercise.requiredRange?.max)))
        list.add(SimpleValue("Инвентарь", exercise.inventory?.map { it.name }?.joinToString()))
        exercise.muscle_groups?.let {
            txt_muscles.text = "Работа мышц: " + exercise.muscle_groups?.map { it.name }?.joinToString("+")

        }
        exercise.estimateTime?.let { time ->
            txt_time.text = convertTimeToString(time.toLong())
        }
        rv_exercise_attrs.adapter = adapter
        adapter.data = list as ArrayList<Any>
        adapter.notifyDataSetChanged()
        txt_ex_desc.text = exercise.description
        txt_ex_name.text = exercise.name

        if (onlyShow) btn_start_or_sub.hide()

        btn_start_or_sub.setOnClickListener {
            controller.navigate(R.id.action_global_exercisesExecutionHostFragment, ExercisesExecutionHostFragment.openForExercise(exercise))
        }

    }

    inner class ExerciseAttrs(val context: Context) : BaseAdapter(R.layout.exercise_attr) {

        init {

            count = 2

            onBind { holder, pos ->

                val title = holder.find<TextView>(R.id.txt_attr_title)
                val value = holder.find<TextView>(R.id.txt_attr_value)
                val v = data[pos] as SimpleValue
                title.text = v.key
                value.text = v.value

            }
        }

    }

    data class SimpleValue(val key: String, val value: String?)

}
