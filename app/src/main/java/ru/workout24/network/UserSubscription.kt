package ru.workout24.network

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity
data class UserSubscription(
    @PrimaryKey
    @SerializedName("id")
    var subId: String,
    @Embedded
    @SerializedName("subscription")
    var subscription: Subscription?,
    @SerializedName("started_at")
    var started_at: String?,
    @SerializedName("expire_at")
    var expire_at: String?
)

data class Subscription(
    var id: String,
    var name: String,
    var price: String,
    var expired_at: String? = null,
    var owned: Boolean = false
)