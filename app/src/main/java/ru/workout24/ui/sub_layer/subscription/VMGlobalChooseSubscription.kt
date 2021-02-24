package ru.workout24.ui.sub_layer.subscription

import ru.workout24.network.Api
import ru.workout24.utills.base.BaseViewModel
import javax.inject.Inject

class VMGlobalChooseSubscription @Inject constructor(val api: Api) : BaseViewModel() {
    var chooseSubnavId: String? = null
}