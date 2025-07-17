package io.bossdogs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.bossdogs.MainActivity.Companion.BREED
import io.bossdogs.MainActivity.Companion.IMAGES
import io.bossdogs.R
import io.bossdogs.databinding.FragmentBreedsBinding
import timber.log.Timber

@AndroidEntryPoint
class BreedsFragment : Fragment() {

    private var _binding: FragmentBreedsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BreedsViewModel by viewModels()
    private val adapter = BreedsAdapter(
        onClick = { breed ->
            openImages(breed)
        },
        onImageRequest = { breed ->
            viewModel.loadBreedImage(breed)
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreedsBinding.inflate(
            inflater,
            container,
            false
        )
        _binding?.viewModel = viewModel
        _binding?.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("💡 BreedsFragment onViewCreated")
        setupToolbar()
        setupRecyclerView()
        setupRetry()
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            show()
            title = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
        binding.recyclerView.adapter = adapter
    }

    private fun setupRetry() {
        binding.retryButton.setOnClickListener { viewModel.retry() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openImages(breedName: String) {
        val imagesFragment = ImagesFragment()
        imagesFragment.arguments = bundleOf(
            BREED to breedName.lowercase()
        )
        activity?.supportFragmentManager?.beginTransaction()
            ?.add(
                R.id.fragment_container,
                imagesFragment,
                IMAGES
            )
            ?.addToBackStack(IMAGES)?.commit()
    }
}