package io.bossdogs.ui

import androidx.recyclerview.widget.DiffUtil

class DiffCallback<T>(
    private val itemsSame: (old: T, new: T) -> Boolean,
    private val contentsSame: (old: T, new: T) -> Boolean = { a, b -> a == b }
) : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(
        oldItem: T & Any,
        newItem: T & Any
    ): Boolean = itemsSame(oldItem, newItem)

    override fun areContentsTheSame(
        oldItem: T & Any,
        newItem: T & Any
    ): Boolean = contentsSame(oldItem, newItem)
}
