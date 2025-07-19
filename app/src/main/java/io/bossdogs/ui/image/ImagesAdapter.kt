package io.bossdogs.ui.image

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.bossdogs.databinding.ItemImageBinding
import io.bossdogs.model.DogImage
import io.bossdogs.ui.DiffCallback

class ImagesAdapter(
    private val onClick: (DogImage) -> Unit
) : ListAdapter<DogImage, ImagesAdapter.ImageViewHolder>(
    DiffCallback(
        itemsSame = { a, b -> a.url == b.url }
    )
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ImageViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(img: DogImage) {
            binding.image = img
            binding.root.setOnClickListener { onClick(img) }
            binding.executePendingBindings()
        }
    }
}