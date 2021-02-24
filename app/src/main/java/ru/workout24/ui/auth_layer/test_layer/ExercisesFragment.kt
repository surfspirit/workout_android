package ru.workout24.ui.auth_layer.test_layer


import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import ru.workout24.R
import ru.workout24.ui.auth_layer.register_login.RegisterLoginFragment
import ru.workout24.ui.auth_layer.test_layer.pojos.Exercise
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.convertTimeToString
import ru.workout24.utills.custom_views.CustomAwesomeVimeoPlayer
import kotlinx.android.synthetic.main.fragment_exercises.*
import org.jetbrains.anko.find
import ru.workout24.utills.getCountByRange

/**
 * ViewPager с детализацией упражнения
 */
class ExercisesFragment : BaseFragment(R.layout.fragment_exercises) {

    private var currentPosition = 0
        set(value) {
            view?.find<ViewPager2>(R.id.vp_exercise)?.currentItem = value
            field = value
        }

    private val viewModel: VMFitnessTest by lazy {
        attachViewModel<VMFitnessTest>(VMFitnessTest::class.java, false) { code, message -> }
    }

    private val adapter: ExercisesAdapter by lazy {
        ExercisesAdapter(context!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarLayout.setOnBackClick { controller.popBackStack() }

        adapter.setOnPlayClick { pos ->
            //TODO("ТИПА ЗДЕСЬ ВОСПРОИЗВОДИТСЯ ВИДЕО")
        }

        vp_exercise.adapter = adapter
        currentPosition = arguments?.getInt(RegisterLoginFragment.POS_KEY, 0) ?: 0

        vp_exercise.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                vp_exercise?.setCurrentItem(position, true)
                when {
                    position == 0 -> preview.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.darkslategray))
                    adapter.data.size - 1 == position -> {
                        next.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.darkslategray))
                        preview.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.coral))
                    }

                    else -> {
                        next.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.coral))
                        preview.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.coral))
                    }
                }
                when {
                    position == 0 -> preview.imageTintList =
                        ColorStateList.valueOf(resources.getColor(R.color.darkslategray))
                    adapter.data.size - 1 == position -> {
                        next.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.darkslategray))
                        preview.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.coral))
                    }

                    else -> {
                        next.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.coral))
                        preview.imageTintList =
                            ColorStateList.valueOf(resources.getColor(R.color.coral))
                    }
                }


                txt_exercise_count.text = "Упражнение №${position + 1}"
            }
        })
        viewModel.exercises.observe(this, Observer {
            it?.let { exercises ->
                adapter.data = exercises as ArrayList<Any>
                if (currentPosition != 0) currentPosition = currentPosition
                adapter.notifyDataSetChanged()

            }
        })

        preview.setOnClickListener {
            if (vp_exercise.currentItem - 1 >= 0) {
                currentPosition = vp_exercise.currentItem - 1
            }
        }
        next.setOnClickListener {
            if (adapter.data.size > vp_exercise.currentItem + 1) {
                currentPosition = vp_exercise.currentItem + 1
            }
        }
    }

    companion object {
        val POS_KEY = "pos"

        fun getBundle(pos: Int): Bundle {
            val args = Bundle()

            args.putInt(POS_KEY, pos)

            return args
        }
    }
}

class ExercisesAdapter(val context: Context) :
    BaseAdapter(R.layout.fragment_test_exercise_description) {
    private var onPlayCallback: (pos: Int) -> Unit = {}

    init {

        onBind { holder, pos ->
            val item = data[pos] as Exercise
            val adapter = ExerciseAttrs(item)
            val rv = holder.find<RecyclerView>(R.id.rv_exercise_attrs)
            val desc = holder.find<TextView>(R.id.textView10)
            val name = holder.find<TextView>(R.id.textView4)
            val txt_time = holder.find<TextView>(R.id.txt_time)
            val playerView = holder.find<CustomAwesomeVimeoPlayer>(R.id.playerView)
            rv.adapter = adapter
            desc.text = item.description
            name.text = item.name
            item.estimate_time?.let {
                txt_time.text = convertTimeToString(it.toLong())

            }
            item.videoUrl?.let {
                playerView.initVimeoPlayer(it)

            }
        }
    }

    fun setOnPlayClick(callback: (pos: Int) -> Unit) {
        onPlayCallback = callback
    }


    inner class ExerciseAttrs(val exercise: Exercise) : BaseAdapter(R.layout.exercise_attr) {
        init {
            count = 2
            onBind { holder, pos ->
                val title = holder.find<TextView>(R.id.txt_attr_title)
                val value = holder.find<TextView>(R.id.txt_attr_value)
                when (pos) {
                    0 -> {
                        title.text = "Повторов"

                        value.text = getCountByRange(
                            exercise.requiredRange?.min,
                            exercise.requiredRange?.max
                        )
                    }
                    1 -> {
                        title.text = "Инвентарь"
                        value.text = exercise.inventory?.map { it.name }?.joinToString()
                    }
                }
            }
        }
    }
}
