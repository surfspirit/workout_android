package ru.workout24.ui.statistics_layer.statistics

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.workout24.R
import ru.workout24.ui.statistics_layer.edit_statistics_entry.EditStatisticsEntryFragment
import ru.workout24.network.UserStatistic
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_statistics.*
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import ru.workout24.network.ResourceProvider
import ru.workout24.ui.statistics_layer.statistics.dto.UserStatsQueryDto
import ru.workout24.utills.*
import java.util.*
import java.text.SimpleDateFormat

class StatisticsDateFragment : BaseFragment(R.layout.fragment_statistics_date) {
    private val adapter: StatisticsDateFragmentAdapter by lazy {
        attachAdapter(StatisticsDateFragmentAdapter(context!!))
    }

    lateinit var userStatistics: ResourceProvider.UserStatisticsResource

    val parser by lazy {
        SimpleDateFormat(STATISTIC_DATE_PATTERN, Locale.getDefault())
    }
    val formatter by lazy {
        SimpleDateFormat("HH:mm")
    }
    var query = UserStatsQueryDto()
    var showProgress = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.setOnBackClick {
            controller.popBackStack()
        }
        userStatistics = resourceFactory.userStatisticsResource

        val date = arguments?.getString("statistic_date")
        if (date.isNullOrEmpty()) {
            controller.popBackStack()
        } else {
            val parser = SimpleDateFormat("yyyy-MM-dd")
            val formatter = SimpleDateFormat("dd.MM.yyyy")
            try {
                appBarLayout.setTitleText("Статистика ${formatter.format(parser.parse(date))}")
            } catch (e: Exception){
                Log.e(this::javaClass.name, "Пришел некорректный формат данных $STATISTIC_DATE_PATTERN")
            }
        }

        rv_stat.show()
        appBarLayout.setOnPic1Click {
            controller.navigate(
                R.id.action_statisticsDateFragment_to_editStatisticsEntryFragment,
                EditStatisticsEntryFragment.getAddStatisticsEntryBundle(DateItemType.ITEM_MANUAL)
            )
        }
        appBarLayout.setPic1Drawable(R.drawable.ic_plus)
        adapter.data = arrayListOf()
        rv_stat.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnClick {
            controller.navigate(
                R.id.action_statisticsDateFragment_to_editStatisticsEntryFragment,
                EditStatisticsEntryFragment.getEditStatisticsEntryBundle(
                    it.userStatistic,
                    it.type
                )
            )
        }

        rv_stat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                rv_stat.layoutManager?.let { layoutManager ->
                    val visibleItemCount = layoutManager.childCount;
                    val totalItemCount = layoutManager.itemCount;
                    val firstVisibleItemPosition =
                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition();
                    if (!showProgress && userStatistics.hasMore) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= USER_STATS_PAGE_SIZE
                        ) {
                            showProgress = true
                            query.page = totalItemCount / USER_STATS_PAGE_SIZE + 1
                            userStatistics.load()
                        }
                    }
                }
            }
        })

        userStatistics.onChange(this, { list ->
            val adapterList = arrayListOf<Any>()
            showProgress = false
            for (item in list) {
                when (item.targetType) {
                    "TRAINING_RESULT", "SINGLE_EXERCISE_RESULTS", "NONE", "TEST_EXERCISE_RESULTS" -> item.target?.name?.let {
                        adapterList.add(
                            DateItem(
                                name = item.target.name,
                                duration = formatter.format(parser.parse(item.createdAt)),
                                type = DateItemType.ITEM_AUTO,
                                userStatistic = item
                            )
                        )
                    }
                    else -> item.target?.name?.let {
                        adapterList.add(
                            DateItem(
                                name = item.target.name,
                                type = DateItemType.ITEM_MANUAL,
                                userStatistic = item
                            )
                        )
                    }
                }
            }
            adapter.data = adapterList
            adapter.notifyDataSetChanged()
        }, {
            showInfoAlert(
                context!!,
                "Ошибка",
                "Произошла ошибка во время обработки запроса",
                "OK",
                {}).show()
        })

        query.day = date!!
        query.limit = USER_STATS_PAGE_SIZE
        query.page = 1
        userStatistics.query = query
        showProgress = true
        userStatistics.load()


    }

    class StatisticsDateFragmentAdapter(val context: Context) :
        BaseAdapter(R.layout.item_statistics_date) {

        var clickCallback: (DateItem) -> Unit = {}

        init {


            onBind { holder, pos ->
                val tv_time = holder.find<TextView>(R.id.tv_time)
                val tv_name = holder.find<TextView>(R.id.tv_name)
                val iv_check = holder.find<ImageView>(R.id.iv_check)
                val item = (data[pos]) as DateItem

                tv_time.text = item.duration
                tv_name.text = item.name
                if (item.type == DateItemType.ITEM_MANUAL) {
                    tv_name.textColor = Color.parseColor("#9B9B9B")
                    iv_check.hide()
                }

                holder.setOnClickListener {
                    clickCallback(data[pos] as DateItem)
                }

            }


        }

        fun setOnClick(clickCallback: (DateItem) -> Unit) {
            this.clickCallback = clickCallback
        }
    }

    enum class DateItemType(@IdRes val navigateId: Int) {
        ITEM_AUTO(R.id.statisticsFragment),
        ITEM_MANUAL(R.id.action_editStatisticsEntryFragment_to_statisticsTrainingsFilterFragment)
    }

    data class DateItem(
        val name: String = "",
        val duration: String = "",
        val type: DateItemType = DateItemType.ITEM_AUTO,
        val userStatistic: UserStatistic
    )
}