package ru.workout24.ui.wg_bonuses_detail.info_new_opportunities

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import ru.workout24.R
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_info_new_opportunities.*


class InfoNewOpportunitiesFragment : BaseFragment(R.layout.fragment_info_new_opportunities) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarLayout.setOnBackClick { controller.popBackStack() }
        textView19.setText(Html.fromHtml(resources.getString(R.string.info_new_opp_text)), TextView.BufferType.SPANNABLE)
    }
}
