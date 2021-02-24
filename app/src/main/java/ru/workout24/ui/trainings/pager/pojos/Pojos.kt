package ru.workout24.ui.trainings.pager.pojos

import android.os.Parcel
import android.os.Parcelable
import ru.workout24.ui.trainings.once_exercise.Inventories
import ru.workout24.ui.trainings.once_exercise.MuscleGroup
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup


class Criteria(
    var name: String? = null,
    var isFavorite: Boolean = false,
    var serverName: String? = null,
    var value: Any? = null,
    var filterId: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeByte(if (isFavorite) 1 else 0)
        parcel.writeString(serverName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Criteria> {
        override fun createFromParcel(parcel: Parcel): Criteria {
            return Criteria(parcel)
        }

        override fun newArray(size: Int): Array<Criteria?> {
            return arrayOfNulls(size)
        }
    }
}
class Category(title: String, items: List<Criteria>, val iconResId: Int) : ExpandableGroup<Criteria>(title, items) {

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is Category) return false

        val genre = o as Category?

        return iconResId == genre!!.iconResId

    }

    override fun hashCode(): Int {
        return iconResId
    }
}
data class MuscleAndInventories(val muscleGroup: List<MuscleGroup>?, val inventories: List<Inventories>?)
