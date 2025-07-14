package io.bossdogs.ui

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import io.bossdogs.R
import io.bossdogs.databinding.ItemBreedBinding
import io.bossdogs.model.DogBreed
import timber.log.Timber

class DogBreedsAdapter(
    private val onClick: (String) -> Unit,
    private val onImageRequest: (String) -> Unit
) : ListAdapter<DogBreed, DogBreedsAdapter.BreedViewHolder>(BreedDiffCallback()) {
    private var images: Map<String, String> = emptyMap()

    fun updateImages(newImages: Map<String, String>) {
        images = newImages
        notifyDataSetChanged()
    }

    /** For your Alphabet sidebar: find the position of the first breed starting with `letter` */
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
            binding.root.setOnClickListener { onClick(breed.name) }

            val imageUrl = images[breed.name]
            if (imageUrl != null) {
                Glide.with(binding.breedImage.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            Timber.e(e, "Glide failed for $imageUrl")
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: com.bumptech.glide.request.target.Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            return false
                        }
                    })
                    .into(binding.breedImage)
            } else {
                binding.breedImage.setImageResource(R.drawable.ic_placeholder)
                onImageRequest(breed.name)
            }

            binding.executePendingBindings()
        }
    }
}

private class BreedDiffCallback : DiffUtil.ItemCallback<DogBreed>() {
    override fun areItemsTheSame(old: DogBreed, new: DogBreed) = old.name == new.name
    override fun areContentsTheSame(old: DogBreed, new: DogBreed) = old == new
}