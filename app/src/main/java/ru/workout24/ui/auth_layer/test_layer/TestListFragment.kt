package ru.workout24.ui.auth_layer.test_layer


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer

import ru.workout24.R
import ru.workout24.ui.auth_layer.test_layer.pojos.Exercise
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.setHtmlText
import kotlinx.android.synthetic.main.fragment_test_list.*
import org.jetbrains.anko.find
import ru.workout24.ui.exercises_execution.ExercisesExecutionHostFragment

/**
 * Экран со списком тестовых упражнений
 */

class TestListFragment : BaseFragment(R.layout.fragment_test_list) {

    private val viewModel: VMFitnessTest by lazy {
        attachViewModel<VMFitnessTest>(
            VMFitnessTest::class.java, false
        )
        { code, message ->
            showInfoAlert(context!!, "Ошибка", message, "OK", {}).show()
        }
    }

    private val adapter: SectionsAdapter by lazy {
        attachAdapter(SectionsAdapter(context!!))
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView4.setHtmlText("Пройдите фитнес-тест и укажите свои <font color=\"${resources.getColor(R.color.coral)}\">реальные</font> результаты")

        adapter.setOnItemClick {
            controller.navigate(R.id.action_testListFragment_to_exercisesFragment,
                ExercisesFragment.getBundle(it)
            )
        }

        rv_test_list.adapter = adapter
        appBarLayout.setOnBackClick {
            controller.popBackStack()
        }
        viewModel.getExercises()
        rv_test_list.showProgress()
        viewModel.exercises.observe(this, Observer {
            it?.let { exercises ->
                rv_test_list.showProgress(false)
                adapter.data = exercises as ArrayList<Any>
                adapter.notifyDataSetChanged()
            }
        })

        ll_agree.setOnClickListener {
            rbtn_accept.isChecked = !rbtn_accept.isChecked
        }

        rbtn_accept.setOnCheckedChangeListener { buttonView, isChecked ->
            btn_start.isEnable = isChecked
        }

        btn_start.setOnClickListener {
            viewModel.exerciseSet?.let { controller.navigate(R.id.action_testListFragment_to_exercisesExecutionHostFragment, ExercisesExecutionHostFragment.openForTest(it)) }
        }
    }

}


class SectionsAdapter(context: Context) : BaseAdapter(R.layout.testlist_rv_normal) {
    private var playCallback: (pos: Int) -> Unit = {}

    init {

        onBind { holder, pos ->
            val item = data[pos] as Exercise
            val title = holder.find<TextView>(R.id.txt_title)
            val img = holder.find<ImageView>(R.id.iv_icon)
            val play = holder.find<ImageView>(R.id.iv_play)

            title.text = item.name
            item.previewUrl?.let {
                glide
                    ?.load(it)
                    ?.into(img)
            }
        }
    }

    fun setOnPlayClick(callback: (pos: Int) -> Unit) {
        playCallback = callback
    }
}

