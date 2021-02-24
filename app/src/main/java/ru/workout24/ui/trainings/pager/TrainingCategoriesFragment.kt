package ru.workout24.ui.trainings.pager


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ru.workout24.R
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.hide
import kotlinx.android.synthetic.main.fragment_training_categories.*
import org.jetbrains.anko.find


/**
 * Фрагмент с категориями тренировок
 */

class TrainingCategoriesFragment : BaseFragment(R.layout.fragment_training_categories,true) {
    private val adapter: TrainingCategoriesAdapter by lazy {
        TrainingCategoriesAdapter(context!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.setItemSelectedCallback { pos ->
//            if (pos == 0 || pos == 3)
//                Toast.makeText(activity?.applicationContext, "Не реализовано", Toast.LENGTH_SHORT).show()
            when (pos) {
                0 -> controller.navigate(R.id.onceTrainingListFragment)
                1 -> controller.navigate(R.id.trainingProgramsFragment)
            }

        }
        adapter.setButtonCallback {
            //Toast.makeText(activity?.applicationContext, "Кнопка нажата", Toast.LENGTH_SHORT).show()
            //controller.navigate(R.id.playgroundFragment)
            controller.navigate(R.id.globalChooseSubscriptionFragment)

        }
        rv_exercise_sections.adapter = adapter
        /*resourceFactory.userResource.load()
        resourceFactory.userResource.onChange(this, {
            adapter.showSubButton = !it.have_subscription
            adapter.notifyDataSetChanged()
        }, {
            showInfoAlert(context!!,"Ошибка","Произошла ошибка выполнения запроса","ОК",{}).show()
        })*/
    }

    companion object {
        fun newInstance(): TrainingCategoriesFragment {
            val fragment = TrainingCategoriesFragment()

            val args = Bundle()
            fragment.arguments = args

            return fragment
        }
    }


}

class TrainingCategoriesAdapter(val context: Context) : BaseAdapter(R.layout.item_training_section) {


    private var itemSelectedCallback: (pos: Int?) -> Unit = {}
    private var buttonCallback: () -> Unit = {}
    public  var showSubButton = true

    init {
        data = arrayListOf(
//            TrainingSection(
//                "Персональные\n" +
//                        "тренировки",
//                "от твоего виртуального WG тренера",
//                R.drawable.personal_trainings_section_background
//            ),
            TrainingSection(
                "Разовая тенировка",
                "подборка тренировок на 1 день",
                R.drawable.once_trainings_section_background
            ),
            TrainingSection(
                "ПРОГРАММЫ ТРЕНИРОВОК ",
                "на 2 недели /1 месяц / 3 месяца",
                R.drawable.training_program_section_background
            )
//            TrainingSection(
//                "МОИ ТРЕНИРОВКИ ",
//                "Составьте программу самостоятельно",
//                R.drawable.my_trainings_section_background
//            )
        )

        onBind { holder, pos ->
            val sectionDesc = holder.find<TextView>(R.id.tv_section_desc)
            val sectionName = holder.find<TextView>(R.id.tv_section_name)
            val sectionImage = holder.find<ImageView>(R.id.iv_section_image)
            val btnGet = holder.find<Button>(R.id.btn_get_now)

            sectionImage.setOnClickListener {
                itemSelectedCallback(pos)
            }
            btnGet.setOnClickListener {
                buttonCallback()
            }

            if (pos == 0) {
                /*if (showSubButton)
                    btnGet.show()
                else*/
                    btnGet.hide()
            } else {
                btnGet.hide()
            }
            sectionDesc.text = (data[pos] as TrainingSection).desc
            sectionName.text = (data[pos] as TrainingSection).title
            sectionImage.setImageResource((data[pos] as TrainingSection).drawableId)

        }


    }

    fun setItemSelectedCallback(itemSelectedCallback: (pos: Int?) -> Unit) {
        this.itemSelectedCallback = itemSelectedCallback
    }

    fun setButtonCallback(buttonCallback: () -> Unit) {
        this.buttonCallback = buttonCallback
    }


    data class TrainingSection(val title: String, val desc: String, val drawableId: Int)
}