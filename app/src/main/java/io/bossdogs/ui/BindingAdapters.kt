package io.bossdogs.ui

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import io.bossdogs.R
import io.bossdogs.model.DogBreed
import io.bossdogs.model.DogImage
import io.bossdogs.ui.image.ImagesAdapter
import io.bossdogs.ui.list.BreedsAdapter
import timber.log.Timber

@BindingAdapter("breedsList")
fun RecyclerView.bindBreedsList(
    breeds: List<
            DogBreed>?
) {
    if (adapter == null) return
    (adapter as ListAdapter<DogBreed, *>).submitList(breeds ?: emptyList())
}

@BindingAdapter("breedImages")
fun RecyclerView.bindBreedImages(images: Map<String, String>?) {
    if (adapter is BreedsAdapter) {
        (adapter as BreedsAdapter).updateImages(images ?: emptyMap())
    }
}

@BindingAdapter("lettersList")
fun LinearLayout.bindLettersList(breeds: List<DogBreed>?) {
    removeAllViews()
    if (breeds.isNullOrEmpty()) return

    val letters = breeds
        .map { it.displayName.first().uppercaseChar() }
        .distinct()
        .sorted()

    weightSum = letters.size.toFloat()

    val rv = this.rootView.findViewById<RecyclerView>(R.id.recycler_view)

    letters.forEach { letter ->
        val tv = TextView(context).apply {
            text = letter.toString()
            textSize = 12f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(ContextCompat.getColor(context, R.color.black))
            gravity = Gravity.CENTER

            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0,
                1f
            )

            setOnClickListener {
                val pos = (rv?.adapter as? BreedsAdapter)
                    ?.getSectionPosition(letter)
                if (pos != null && pos >= 0) {
                    rv.post {
                        rv.layoutManager?.scrollToPosition(pos)
                    }
                }
            }
        }
        addView(tv)
    }
}


@BindingAdapter("imageUrl")
fun ImageView.bindImageUrl(url: String?) {
    Glide.with(this)
        .load(url)
        .placeholder(R.drawable.ic_placeholder)
        .error(R.drawable.ic_placeholder)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable?>,
                isFirstResource: Boolean
            ): Boolean {
                Timber.e(e, "Glide failed for $url")
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable?>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(this)
}

@BindingAdapter("imagesList")
fun RecyclerView.bindImages(list: List<DogImage>?) {
    if (adapter is ImagesAdapter) {
        (adapter as ImagesAdapter).submitList(list ?: emptyList())
    }
}
