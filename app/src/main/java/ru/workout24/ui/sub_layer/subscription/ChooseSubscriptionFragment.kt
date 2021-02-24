package ru.workout24.ui.sub_layer.subscription


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import ru.workout24.R
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.setHtmlText
import kotlinx.android.synthetic.main.fragment_choose_subscription.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast
import ru.workout24.features.GoogleBilling
import ru.workout24.network.*
import ru.workout24.utills.Preferences
import ru.workout24.utills.toAL
import javax.inject.Inject

/**
 * Список подписок
 */

class ChooseSubscriptionFragment : BaseFragment(R.layout.fragment_choose_subscription) {

    @Inject
    lateinit var billing: GoogleBilling

    private val viewModel: VMChooseSubscription by lazy {
        attachViewModel<VMChooseSubscription>(
            VMChooseSubscription::class.java, true
        )
        { code, message ->

        }
    }

    private val adapter: SubscriptionAdapter by lazy {
        SubscriptionAdapter(context!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textView15.setHtmlText("<font color=\"${resources.getColor(R.color.coral)}\">w</font>g подписка")

        adapter.setOnValueChangedListener { it, id ->
            it?.let {
                viewModel.chooseSubnavId = it
                id?.let { viewModel.subscriptionID = it }
                btn_sub.makeItGray(false)
                btn_sub.setText("Подписаться")
            } ?: run {
                viewModel.chooseSubnavId = -1
                viewModel.subscriptionID = ""
                btn_sub.makeItGray()
                btn_sub.setText("Продолжить без подписки")
            }

        }

        btn_sub.makeItGray()
        billing.userProfileId = pref.userProfileId
        billing.subs.observe(this, Observer { subs ->
            adapter.data = subs.toAL()
            adapter.notifyDataSetChanged()
        })
        billing.buyingSub.observe(this, Observer { sub ->
            sub.expired_at?.let {
                textView14.text = billing.getBuyingSubText(it)
            }
        })
        btn_sub.setOnClickListener {
            if (viewModel.subscriptionID != "") {
                showActionAlert(
                    requireContext(),
                    "Подписка",
                    "Вы действительно хотите приобрести подписку?",
                    "Да",
                    "Нет",
                    {
                        billing.buySub(requireActivity(), viewModel.subscriptionID)
                    }).showIfNeeded()
            } else {
                controller.navigate(R.id.action_global_anket_layer)
            }
        }

        rv_subscriptions.adapter = adapter
        billing.errorMessage.observe(viewLifecycleOwner, Observer { message -> toast(message) })
    }

}

class SubscriptionAdapter(val context: Context) : BaseAdapter(R.layout.subscription_item) {

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
            price.text = "за " + (data[pos] as Subscription).price + " \u20BD"
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
            it.second.find<TextView>(R.id.txt_title)
                .setTextColor(context.resources.getColor(R.color.white))
            it.second.find<TextView>(R.id.txt_price)
                .setTextColor(context.resources.getColor(R.color.coral))
        }

        holder.backgroundColor = context.resources.getColor(R.color.coral)
        title.setTextColor(context.resources.getColor(R.color.white))
        price.setTextColor(context.resources.getColor(R.color.white))

        selectedView = Triple(pos, holder, (data[pos] as Subscription).id)
    }
}