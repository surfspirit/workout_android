package ru.workout24.ui.wg_bonuses_detail


import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import ru.workout24.R
import ru.workout24.network.ResourceProvider
import ru.workout24.ui.wg_bonuses_detail.adapter.DetailType
import ru.workout24.ui.wg_bonuses_detail.adapter.WgBonusesDetailAdapter
import ru.workout24.ui.wg_bonuses_detail.data.viewmodel.DetailHeaderViewModel
import ru.workout24.ui.wg_bonuses_detail.data.viewmodel.DetailViewModel
import ru.workout24.ui.wg_bonuses_detail.data.viewmodel.ProgressViewModel
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.custom_views.TypeDivider
import kotlinx.android.synthetic.main.fragment_wg_bonuses_detail.*
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class WgBonusesDetailFragment : BaseFragment(R.layout.fragment_wg_bonuses_detail) {
    @Inject
    lateinit var resourceProvider: ResourceProvider

    private val id get() = arguments!!.getString(ID_KEY)
    private val adapter = WgBonusesDetailAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBar.setOnBackClick {
            controller.popBackStack()
        }
        rv_bonuses.adapter = adapter
        rv_bonuses.addItemDecoration(
            TypeDivider(
                DetailType.DETAIL.value,
                requireContext().resources.getDimensionPixelSize(R.dimen.divider_height),
                ContextCompat.getColor(requireContext(), R.color.silver),
                requireContext().resources.getDimensionPixelSize(R.dimen.divider_left_margin)
            )
        )
        rv_bonuses.addItemDecoration(
            TypeDivider(
                DetailType.CLICKABLE_DETAIL.value,
                requireContext().resources.getDimensionPixelSize(R.dimen.divider_height),
                ContextCompat.getColor(requireContext(), R.color.silver),
                requireContext().resources.getDimensionPixelSize(R.dimen.divider_left_margin)
            )
        )
        btn_apply.setOnClickListener {
            controller!!.navigate(R.id.action_wg_bonuses_detail_fragment_to_infoNewOpportunities)
        }
        initResource()
    }

    private fun initResource() {
        val resourceDetail = resourceProvider.wgLeaderDetail
        resourceDetail.onChange(this, { data ->
            adapter.setData(
                arrayListOf(
                    DetailHeaderViewModel("Успехи", DetailType.HEADER.value),
                    ProgressViewModel(DetailType.PROGRESS_LINE.value, data.totalWg, data.totalWg + data.wgNextLevel, data.wgNextLevel),
                    DetailHeaderViewModel("Информация о бонусах", DetailType.HEADER.value),
                    DetailViewModel(DetailType.DETAIL.value, "Сегодня", "+${data.todayWg} WG бонус"),
//            DetailViewModel(DetailType.DETAIL.value, "Уровень", "1"), //TODO: пока не отображаем
                    DetailViewModel(DetailType.CLICKABLE_DETAIL.value, "Место", "№${data.place}")
                )
            )
        })
        resourceDetail.query = id
        resourceDetail.load()
    }

    companion object {
        val ID_KEY = "ID_KEY"

        fun getBundle(id: String) = Bundle().apply {
            putString(ID_KEY, id)
        }
    }
}
