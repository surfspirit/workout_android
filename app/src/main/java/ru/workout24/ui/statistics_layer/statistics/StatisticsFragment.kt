package ru.workout24.ui.statistics_layer.statistics

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.workout24.R
import ru.workout24.ui.statistics_layer.edit_statistics_entry.EditStatisticsEntryFragment
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.hide
import ru.workout24.utills.show
import ru.workout24.utills.toArrayList
import kotlinx.android.synthetic.main.fragment_statistics.*
import org.jetbrains.anko.find
import ru.workout24.network.ResourceProvider
import ru.workout24.ui.statistics_layer.statistics.dto.UserStatsDate
import ru.workout24.ui.statistics_layer.statistics.dto.UserStatsDatesQuery
import ru.workout24.utills.USER_STATS_DATES_PAGE_SIZE
import java.text.SimpleDateFormat


class StatisticsFragment : BaseFragment(R.layout.fragment_statistics) {
    private val adapter: StatisticsFragmentAdapter by lazy {
        attachAdapter(StatisticsFragmentAdapter(context!!))
    }

    lateinit var userDates:ResourceProvider.UserStatisticsDatesResource

    var showProgress = true
    var query = UserStatsDatesQuery()
    var data = listOf<UserStatsDate>()
    var from_lk = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userDates = resourceFactory.userStatisticsDatesResource
        arguments?.getBoolean("from_lk")?.let {
            from_lk = it
        }
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (from_lk) {
                    controller.popBackStack()
                } else {
                    controller.setGraph(R.navigation.menu_layer)
                }
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        appBarLayout.setOnBackClick {
            if (from_lk) {
                controller.popBackStack()
            } else {
                controller.setGraph(R.navigation.menu_layer)
            }
            progressBlack.hide()
        }
        cl_empty.hide()
        rv_stat.hide()
        btn_create.setOnClickListener {
            controller.navigate(
                R.id.action_statisticsFragment_to_editStatisticsEntryFragment,
                EditStatisticsEntryFragment.getAddStatisticsEntryBundle(
                    StatisticsDateFragment.DateItemType.ITEM_MANUAL
                )
            )
        }
        appBarLayout.setOnPic1Click {
            controller.navigate(
                R.id.action_statisticsFragment_to_editStatisticsEntryFragment,
                EditStatisticsEntryFragment.getAddStatisticsEntryBundle(
                    StatisticsDateFragment.DateItemType.ITEM_MANUAL
                )
            )
        }
        appBarLayout.setPic1Drawable(R.drawable.ic_plus)
        adapter.data = arrayListOf(
        )
        rv_stat.adapter = adapter
        adapter.notifyDataSetChanged()
        adapter.setOnItemClick { pos ->
            val bundle = bundleOf("statistic_date" to data[pos].date)
            controller.navigate(R.id.action_statisticsFragment_to_statisticsDateFragment, bundle)
        }
        rv_stat.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                rv_stat.layoutManager?.let { layoutManager ->
                    val visibleItemCount = layoutManager.childCount;
                    val totalItemCount = layoutManager.itemCount;
                    val firstVisibleItemPosition =
                        (layoutManager as LinearLayoutManager).findFirstVisibleItemPosition();
                    if (!showProgress && userDates.hasMore) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= USER_STATS_DATES_PAGE_SIZE
                        ) {
                            showProgress = true
                            progressBlack.show()
                            query.page = totalItemCount/USER_STATS_DATES_PAGE_SIZE+1
                            userDates.load()
                        }
                    }
                }
            }
        })

        userDates.onChange(this, {
            val list = it.data?.map { UserStatsDate(it) } ?: emptyList()
            progressBlack.hide()
            showProgress = false
            if (list.isNotEmpty()) {
                adapter.data = list.toArrayList()
                data = list
                adapter.notifyDataSetChanged()
                cl_empty.hide()
                rv_stat.show()
            } else {
                rv_stat.hide()
                cl_empty.show()
            }
        }, {
            showInfoAlert(
                context!!,
                "Ошибка",
                "Произошла ошибка во время обработки запроса",
                "OK",
                {}).show()
        })
        query.page = 1
        query.limit = USER_STATS_DATES_PAGE_SIZE
        userDates.query = query
        userDates.clear()
        userDates.load()
        if (showProgress)
            progressBlack.show()
    }

    override fun onResume() {
        super.onResume()
        if (showProgress)
            progressBlack.show()
    }

    override fun onPause() {
        super.onPause()
        progressBlack.hide()
    }

    class StatisticsFragmentAdapter(val context: Context) : BaseAdapter(R.layout.item_statistics) {

        val parser = SimpleDateFormat("yyyy-MM-dd")
        val formatter = SimpleDateFormat("dd.MM.yyyy")


        init {
            onBind { holder, pos ->
                val tv_date = holder.find<TextView>(R.id.tv_date)
                val item = (data[pos]) as UserStatsDate



                tv_date.text = formatter.format(parser.parse(item.date))

            }


        }


    }
}