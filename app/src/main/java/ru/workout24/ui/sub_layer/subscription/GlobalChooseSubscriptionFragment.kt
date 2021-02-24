package ru.workout24.ui.sub_layer.subscription


import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer
import ru.workout24.R
import ru.workout24.network.Subscription
import ru.workout24.utills.base.BaseAdapter
import ru.workout24.utills.base.BaseFragment
import ru.workout24.utills.setHtmlText
import ru.workout24.utills.show
import kotlinx.android.synthetic.main.fragment_global_choose_subscription.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.toast
import ru.workout24.features.GoogleBilling
import ru.workout24.utills.toAL
import javax.inject.Inject

/**
 * Список подписок
 */

class GlobalChooseSubscriptionFragment :
    BaseFragment(R.layout.fragment_global_choose_subscription) {

    @Inject
    lateinit var billing: GoogleBilling

    private val viewModel: VMGlobalChooseSubscription by lazy {
        attachViewModel<VMGlobalChooseSubscription>(
            VMGlobalChooseSubscription::class.java, true
        )
        { code, message ->

        }
    }

    private val adapter: SubscriptionAdapterGlobal by lazy {
        SubscriptionAdapterGlobal(context!!)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //arguments?.getBoolean("show_back_button")?.let {
        //    if (it) {
                iv_back.show()
                iv_back.setOnClickListener {
                    controller.popBackStack()
                }
        //    }
        //}

        textView15.setHtmlText("<font color=\"${resources.getColor(R.color.coral)}\">w</font>g подписка")

        adapter.setOnValueChangedListener {
            it?.let {
                viewModel.chooseSubnavId = (adapter.data[it] as Subscription).id
                btn_sub.makeItGray(false)
                //btn_sub.setText("Подписаться")
            } ?: run {
                btn_sub.makeItGray()
                //btn_sub.setText("Продолжить без подписки")
            }

        }

        btn_sub.makeItGray()

        btn_sub.setOnClickListener {
            viewModel.chooseSubnavId?.let { subId ->
                showActionAlert(requireContext(), "Подписка", "Вы действительно хотите приобрести подписку?", "Да", "Нет", {
                    billing.buySub(requireActivity(), subId)
                }).showIfNeeded()
            }
        }

        rv_subscriptions.adapter = adapter

        billing.userProfileId = pref.userProfileId
        billing.subs.observe(viewLifecycleOwner, Observer { subs ->
            adapter.data = subs.toAL()
            adapter.notifyDataSetChanged()
        })
        billing.buyingSub.observe(this, Observer { sub ->
            sub.expired_at?.let {
                textView14.text = billing.getBuyingSubText(it)
            }
        })
        billing.errorMessage.observe(viewLifecycleOwner, Observer { message -> toast(message) })
    }
}

class SubscriptionAdapterGlobal(val context: Context) : BaseAdapter(R.layout.subscription_item) {

    private var selectedView: Pair<Int, View>? = null
        set(value) {
            valueChangesCallback(value?.first)
            field = value
        }
    private var valueChangesCallback: (pos: Int?) -> Unit = {}


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

    fun setOnValueChangedListener(valueChangesCallback: (pos: Int?) -> Unit) {
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

        selectedView = Pair(pos, holder)
    }

    //data class SubItem(val title: String, val price: String)
}