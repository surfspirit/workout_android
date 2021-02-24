package ru.workout24.ui.sub_layer.subscription

import ru.workout24.R
import ru.workout24.network.Api
import ru.workout24.utills.base.BaseViewModel
import javax.inject.Inject

class VMChooseSubscription @Inject constructor(var api: Api) : BaseViewModel() {
    var subscriptionID:String = ""
    var chooseSubnavId: Int = -1
        get() {
            return when (field) {
                -1 -> R.id.action_chooseSubscriptionFragment_to_sureNotSubscribeFragment
                else -> R.id.action_chooseSubscriptionFragment_to_bestSubscriptionDecision
            }
        }

    var subPrevNavId: Int = -1
        get() {
            return when (field) {
                -1 -> R.id.action_subscriptionPagerFragment_to_sureNotSubscribeFragment
                else -> R.id.action_subscriptionPagerFragment_to_bestSubscriptionDecision
            }
        }
}