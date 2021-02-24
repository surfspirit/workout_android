package ru.workout24.ui

import ru.workout24.network.Api
import ru.workout24.utills.Preferences
import ru.workout24.utills.base.BaseViewModel
import javax.inject.Inject

class RootViewModel @Inject constructor(val api: Api, val pref: Preferences) : BaseViewModel() {
    companion object {

    }


}