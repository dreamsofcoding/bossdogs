package io.bossdogs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.bossdogs.R
import io.bossdogs.databinding.ItemImageBinding
import io.bossdogs.model.DogImage

class ImagesAdapter(
    private val onClick: (DogImage) -> Unit
) : ListAdapter<DogImage, ImagesAdapter.ImageViewHolder>(Diff()) {

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
            Glide.with(binding.root)
                .load(img.url)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_placeholder)
                .into(binding.imageView)

            binding.root.setOnClickListener { onClick(img) }
        }
    }

    private class Diff : DiffUtil.ItemCallback<DogImage>() {
        override fun areItemsTheSame(a: DogImage, b: DogImage) = a.url == b.url
        override fun areContentsTheSame(a: DogImage, b: DogImage) = a == b
    }
}