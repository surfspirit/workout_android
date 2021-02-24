package ru.workout24.network


import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity
data class User(
    @SerializedName("active")
    val active: Boolean?,
    @SerializedName("have_subscription")
    val have_subscription: Boolean = false,
    @SerializedName("avatar_url")
    val avatar_url: String?,
    @SerializedName("bdate")
    val bdate: String?,
    @SerializedName("completed_test")
    val completedTest: Boolean?,
    @Embedded
    @SerializedName("current_stats")
    val currentStats: CurrentStatsDto?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("fb_id")
    val fbId: String?,
    @SerializedName("gender")
    val gender: String?,
    @SerializedName("goals")
    val goals: String?,
    @SerializedName("google_id")
    val googleId: String?,
    @PrimaryKey
    @SerializedName("id")
    val id: String,
    @SerializedName("last_activity")
    val lastActivity: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("notifications")
    val notifications: Boolean?,
    @SerializedName("registration_date")
    val registrationDate: String?,
    @SerializedName("role")
    val role: String?,
    @SerializedName("surname")
    val surname: String?,
    @SerializedName("skip_test")
    val skip_test: Boolean?,
    @SerializedName("training_level")
    val trainingLevel: String?,
    @SerializedName("trainings_in_week")
    val trainingsInWeek: Int?,
    @SerializedName("vk_id")
    val vkId: String?
)

data class CurrentStatsDto(
    @SerializedName("arm_size")
    val armSize: Float?,
    @SerializedName("chest_size")
    val chestSize: Float?,
    @SerializedName("fat_percentage")
    val fatPercentage: Float?,
    @SerializedName("height")
    val height: Float?,
    @SerializedName("neck_size")
    val neckSize: Float?,
    @SerializedName("thigh_size")
    val thighSize: Float?,
    @SerializedName("waist_size")
    val waistSize: Float?,
    @SerializedName("weight")
    val weight: Float?
)