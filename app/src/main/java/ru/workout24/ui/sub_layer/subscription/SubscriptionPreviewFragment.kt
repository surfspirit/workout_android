package ru.workout24.ui.sub_layer.subscription


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import ru.workout24.R
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_subscription_preview.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast
import ru.workout24.features.GoogleBilling
import ru.workout24.network.*
import ru.workout24.utills.toAL
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 *
 */
class SubscriptionPreviewFragment : BaseFragment(R.layout.fragment_subscription_preview) {

    @Inject
    lateinit var billing: GoogleBilling

    private val viewModel: VMChooseSubscription by lazy {
        attachViewModel<VMChooseSubscription>(
            VMChooseSubscription::class.java, true
        )
        { code, message ->

        }
    }

    private val adapter: SubscriptionListAdapter by lazy {
        SubscriptionListAdapter(context!!)
    }

    companion object {
        fun newInstance(fragmentIndex: Int): SubscriptionPreviewFragment {
            val fragment = SubscriptionPreviewFragment()

            val args = Bundle()
            args.putInt("fragmentIndex", fragmentIndex)
            fragment.arguments = args

            return fragment
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parameter = arguments?.getInt("fragmentIndex")
        when (parameter) {
            0 -> {
                tvSubscriptionTitle.text = "программы тренировок"
                tvSubscriptionDescription.text =
                    "Подписка предоставляет доступ ко всем программам тренировок, включая тренировки с индивидуальным подходом, включая доступ к «WG тренер»"
                ivSubscriptionPreview.isVisible = true
                ivSubscriptionPreview.setImageResource(R.drawable.subscription1_background)
                clSubscriptionList.isVisible = false
            }

            1 -> {
                tvSubscriptionTitle.text = "Упражнения"
                tvSubscriptionDescription.text =
                    "Позволяют составлять собственную программу тренировок на любые группы мышц и цели"
                ivSubscriptionPreview.isVisible = true
                ivSubscriptionPreview.setImageResource(R.drawable.subscription2_background)
                clSubscriptionList.isVisible = false
            }

            2 -> {
                tvSubscriptionTitle.text = "полезная информация"
                tvSubscriptionDescription.text =
                    "Дарит знания о поступлении и расходе калорий, правильном питании, диетах и многом другом"
                ivSubscriptionPreview.isVisible = true
                ivSubscriptionPreview.setImageResource(R.drawable.subscription3_background)
                clSubscriptionList.isVisible = false
            }

            3 -> {
                tvSubscriptionTitle.text = "подписка WG"
                tvSubscriptionDescription.text =
                    "Вы в любой момент сможете поменять настройки подписки или отключить ее в настройках приложения"
                ivSubscriptionPreview.isVisible = false
                ivSubscriptionPreview.setImageResource(R.drawable.subscription3_background)
                clSubscriptionList.isVisible = true
            }
        }

        adapter.setOnValueChangedListener { it, id ->
            it?.let {
                viewModel.chooseSubnavId = it
                id?.let { viewModel.subscriptionID = it }
                btn_register.makeItGray(false)
                btn_register.setText("Подписаться")
            } ?: run {
                viewModel.chooseSubnavId = -1
                viewModel.subscriptionID = ""
                btn_register.makeItGray()
                btn_register.setText("Продолжить без подписки")
            }

        }

        rv_sub_list.adapter = adapter

        billing.userProfileId = pref.userProfileId
        billing.subs.observe(viewLifecycleOwner, Observer { subs ->
            adapter.data = subs.toAL()
            adapter.notifyDataSetChanged()
        })
        billing.buyingSub.observe(this, Observer { sub ->
            sub.expired_at?.let {
                tvSubscriptionDescription.text = billing.getBuyingSubText(it)
            }
        })
        btn_register.makeItGray()
        btn_register.setOnClickListener {
            if (viewModel.subscriptionID != "") {
                showActionAlert(requireContext(), "Подписка", "Вы действительно хотите приобрести подписку?", "Да", "Нет", {
                    billing.buySub(requireActivity(), viewModel.subscriptionID)
                }).showIfNeeded()
            } else{
                controller.navigate(R.id.action_global_anket_layer)
            }
        }
        billing.errorMessage.observe(viewLifecycleOwner, Observer { message -> toast(message) })
    }

}


class SubscriptionListAdapter(val context: Context) : BaseAdapter(R.layout.subscription_item) {

    private var selectedView: Triple<Int, View, String>? = null
        set(value) {
            valueChangesCallback(value?.first, value?.third)
            field = value
        }
    private var valueChangesCallback: (pos: Int?, id: String?) -> Unit = { _, _ -> {} }


    init {
        onBind { holder, pos ->
            val title = holder.find<TextView>(R.id.txt_title)
            val price = holder.find<TextView>(R.id.txt_price)

            holder.setOnClickListener {
                select(pos, holder, title, price)
            }

            title.text = (data[pos] as Subscription).name
            price.text = "за " + (data[pos] as Subscription).price.toString() + " \u20BD"

        }
    }

    fun setOnValueChangedListener(valueChangesCallback: (pos: Int?, id: String?) -> Unit) {
        this.valueChangesCallback = valueChangesCallback
    }

    fun select(pos: Int, holder: View, title: TextView, price: TextView) {

        if (pos == selectedView?.first) {
            holder.backgroundColor = context.resources.getColor(R.color.darkslategray)
            title.setTextColor(context.resources.getColor(R.color.white))
            price.setTextColor(context.resources.getColor(R.color.coral))

            selectedView = null

            return
        }

        selectedView?.let {
            it.second.backgroundColor = context.resources.getColor(R.color.darkslategray)
            it.second.find<TextView>(R.id.txt_title).setTextColor(context.resources.getColor(R.color.white))
            it.second.find<TextView>(R.id.txt_price).setTextColor(context.resources.getColor(R.color.coral))
        }

        holder.backgroundColor = context.resources.getColor(R.color.coral)
        title.setTextColor(context.resources.getColor(R.color.white))
        price.setTextColor(context.resources.getColor(R.color.white))

        selectedView = Triple(pos, holder, (data[pos] as Subscription).id)
    }
}
