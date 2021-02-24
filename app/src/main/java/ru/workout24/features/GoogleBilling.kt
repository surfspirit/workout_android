package ru.workout24.features

import android.app.Activity
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.billingclient.api.*
import ru.workout24.network.Subscription
import ru.workout24.utills.RxSingleLiveData

internal typealias CODE = BillingClient.BillingResponseCode
internal typealias TYPE = BillingClient.SkuType

internal const val SUBS = TYPE.SUBS

class GoogleBilling(applicationContext: Context) : BillingClientStateListener,
    PurchasesUpdatedListener {
    private val billingClient: BillingClient = BillingClient.newBuilder(applicationContext)
        .enablePendingPurchases()
        .setListener(this)
        .build()

    private val _skuDetailsList = hashMapOf<String, SkuDetails>()
    private val _subs = MutableLiveData<List<Subscription>>()
    private val _buyingSub = MutableLiveData<Subscription>()
    private val _errorMessage = MutableLiveData<String>()
    val subs: LiveData<List<Subscription>> get() = _subs
    val buyingSub: LiveData<Subscription> get() = _buyingSub
    val errorMessage: LiveData<String> get() = _errorMessage
    val isSubBuySuccess = RxSingleLiveData<Boolean>()
    var userProfileId: String? = null

    private var _pendingSubId: String? = null

    init {
        billingClient.startConnection(this)
    }

    override fun onBillingServiceDisconnected() {
        //сюда мы попадем если что-то пойдет не так
    }

    override fun onBillingSetupFinished(billingResult: BillingResult?) {
        if (billingResult?.responseCode == CODE.OK) {
            billingClient.querySkuDetailsAsync(
                SkuDetailsParams.newBuilder().setSkusList(
                    mutableListOf("sub1", "sub2", "sub3", "sub4")
                ).setType(SUBS).build(), ::skuList
            )
        } else {
            _errorMessage.postValue(msg(billingResult?.responseCode ?: Int.MIN_VALUE))
        }
    }

    private fun skuList(res: BillingResult?, list: List<SkuDetails>?) {
        if (res?.responseCode == CODE.OK) {
            val ownedIds = getOwnedSkuIds()
            list?.let {
                it.find { ownedIds.contains(it.sku) }?.let { sub -> _buyingSub.postValue(
                    Subscription(
                        id = sub.sku,
                        name = sub.title.split("(")[0],
                        price = sub.price,
                        expired_at = SubsPeriods.valueOf(sub.subscriptionPeriod).text,
                        owned = ownedIds.contains(sub.sku)
                    )
                ) }
                _subs.postValue(it.filter { !ownedIds.contains(it.sku) }.map { details ->
                    _skuDetailsList[details.sku] = details
                    Subscription(
                        id = details.sku,
                        name = details.title.split("(")[0],
                        price = details.price,
                        expired_at = details.subscriptionPeriod,
                        owned = ownedIds.contains(details.sku)
                    )
                })
            }
        } else {
            _errorMessage.postValue(msg(res?.responseCode ?: Int.MIN_VALUE))
        }
    }

    private fun getOwnedSkuIds(): List<String> {
        val result = billingClient.queryPurchases(SUBS)
        return if (result.responseCode == CODE.OK) {
            result.purchasesList.map { it.sku }
        } else {
            emptyList()
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult?,
        purchases: MutableList<Purchase>?
    ) {
        //сюда мы попадем когда будет осуществлена покупка
        if (billingResult?.responseCode == CODE.OK && purchases != null) {
            if (purchases.find { it.sku == _pendingSubId } != null) {
                _pendingSubId = null
                isSubBuySuccess.postValue(true)
                val ownedIds = getOwnedSkuIds()
                ownedIds.forEach { skuId ->
                    val sub = _skuDetailsList[skuId]
                    sub?.let {
                        _buyingSub.postValue(
                            Subscription(
                                id = sub.sku,
                                name = sub.title.split("(")[0],
                                price = sub.price,
                                expired_at = SubsPeriods.valueOf(sub.subscriptionPeriod).text,
                                owned = ownedIds.contains(sub.sku)
                            )
                        )
                    }
                }
                val newSubList = _subs.value?.filter { !ownedIds.contains(it.id) }
                _subs.postValue(newSubList)
            }
        } else {
            _errorMessage.postValue(msg(billingResult?.responseCode ?: Int.MIN_VALUE))
        }
    }

    fun buySub(activity: Activity, subId: String) {
        _pendingSubId = subId
        billingClient.launchBillingFlow(
            activity,
            BillingFlowParams.newBuilder().setObfuscatedAccountId(userProfileId).setSkuDetails(_skuDetailsList[subId]).build()
        )
    }

    private fun msg(r: Int): String = when (r) {
        CODE.SERVICE_UNAVAILABLE, CODE.SERVICE_DISCONNECTED, CODE.SERVICE_TIMEOUT, CODE.BILLING_UNAVAILABLE -> "Платежный сервис недоступен\nПопробуйте совершить покупку позже"
        CODE.DEVELOPER_ERROR -> "Ошибка тестирования платежной системы"
        CODE.FEATURE_NOT_SUPPORTED -> "Платежный сервис не поддерживается на данном устройстве"
        CODE.ITEM_ALREADY_OWNED -> "Подписка уже приобретена"
        CODE.ITEM_NOT_OWNED -> "Подписку не удалось приобрести\nПопробуйте еще раз"
        CODE.ITEM_UNAVAILABLE -> "Подписка недоступна"
        CODE.USER_CANCELED -> "Покупка отменена"
        else -> "Неизвестная ошибка"
    }

    fun getBuyingSubText(period: String) = "У вас уже есть подписка на $period. Вы можете выбрать другой вариант подписки\nОстаток будет пересчитан после покупки"
}

internal enum class SubsPeriods(val text: String) {
    P1Y("год"), P6M("6 месяцев"), P1M("1 месяц"), P3M("3 месяца")
}