package io.bossdogs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import io.bossdogs.MainActivity.Companion.BREED
import io.bossdogs.R
import io.bossdogs.databinding.FragmentImagesBinding
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImagesFragment : Fragment() {

    private var _binding: FragmentImagesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ImagesViewModel by viewModels()
    private lateinit var adapter: ImagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val breedName = requireArguments().getString(BREED)!!
        viewModel.loadImages(breedName)

        adapter = ImagesAdapter { image ->
            viewModel.selectHero(image)
        }
        binding.imagesRecyclerview.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.imagesRecyclerview.adapter = adapter


        lifecycleScope.launch {
            viewModel.uiState.observe(viewLifecycleOwner) { state ->
                binding.loadingView.isVisible = state is UiState.Loading
                binding.errorContainer.isVisible = state is UiState.Error
                binding.imagesRecyclerview.isVisible = state is UiState.Success

                when (state) {
                    is UiState.Success -> {
                        adapter.submitList(state.data)
                        if (state.data.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                R.string.images_empty,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    is UiState.Error -> {
                        binding.errorText.text = state.throwable.toString()
                    }

                    else -> { /* no-op */
                    }
                }
            }

            viewModel.selectedHeroImage.observe(viewLifecycleOwner) { dogImage ->
                dogImage?.let { hero ->
                    Glide.with(this@ImagesFragment)
                        .load(hero.url)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(binding.heroImage)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}