package ru.workout24.utills

import androidx.recyclerview.widget.DiffUtil

abstract class CustomDiffUtil<T>: DiffUtil.Callback() {

    var oldList = listOf<T?>()
    var newList = listOf<T?>()

    override fun areItemsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
        return itemsTheSame(oldList[oldItemPos], newList[newItemPos])
    }

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areContentsTheSame(oldItemPos: Int, newItemPos: Int): Boolean {
        return contentsTheSame(oldList[oldItemPos], newList[newItemPos])
    }

    fun initLists(oldList: List<T?>, newList: List<T?>){
        this.oldList = oldList
        this.newList = newList
    }

    protected abstract fun itemsTheSame(oldItem: T?, newItem: T?): Boolean
    protected abstract fun contentsTheSame(oldItem: T?, newItem: T?): Boolean
}