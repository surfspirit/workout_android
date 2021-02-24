package ru.workout24.network

import ru.workout24.ui.trainings.once_exercise.Inventories
import com.google.gson.annotations.SerializedName

data class InventoryResponse(
    @SerializedName("inventories")
    val inventories:List<Inventories>
)