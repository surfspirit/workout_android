package ru.workout24.ui.statistics_layer.edit_statistics_entry.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.workout24.R
import ru.workout24.ui.statistics_layer.edit_statistics_entry.adapter.holder.PhotoItemHolder

class PhotoAdapter(private val listener: StatisticsEntryListener) :
    RecyclerView.Adapter<PhotoItemHolder>() {

    private var data: ArrayList<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoItemHolder =
        PhotoItemHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_photo_white,
                parent,
                false
            ),
            listener
        )

    override fun onBindViewHolder(holder: PhotoItemHolder, position: Int) {
        holder.bind(data?.elementAtOrNull(position), data?.size != 3 && position == 1)
    }

    override fun getItemCount(): Int = 3

    fun setAdapterData(data: ArrayList<String>) {
        this.data = data
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        data?.removeAt(position)
    }
}