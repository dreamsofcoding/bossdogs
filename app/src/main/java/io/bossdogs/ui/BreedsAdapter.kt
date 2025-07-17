package io.bossdogs.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.bossdogs.databinding.ItemBreedBinding
import io.bossdogs.model.DogBreed

class BreedsAdapter(
    private val onClick: (String) -> Unit,
    private val onImageRequest: (String) -> Unit
) : ListAdapter<DogBreed, BreedsAdapter.BreedViewHolder>(
    DiffCallback(
        itemsSame = { a, b -> a.name == b.name }
    )
) {
    private var images: Map<String, String> = emptyMap()

    fun updateImages(newImages: Map<String, String>) {
        images = newImages
        notifyDataSetChanged()
    }

    fun getSectionPosition(letter: Char): Int? {
        val idx = currentList.indexOfFirst {
            it.displayName.first().uppercaseChar() == letter
        }
        return idx.takeIf { it != -1 }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BreedViewHolder {
        val binding = ItemBreedBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BreedViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BreedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BreedViewHolder(private val binding: ItemBreedBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(breed: DogBreed) {
            binding.breed = breed

            val url = images[breed.name]
            binding.imageUrl = url
            if (url == null) onImageRequest(breed.name)

            binding.root.setOnClickListener { onClick(breed.name) }

            binding.executePendingBindings()
        }
    }
}