package io.bossdogs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.bossdogs.R
import io.bossdogs.databinding.FragmentBreedsBinding
import io.bossdogs.model.DogBreed
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class BreedsFragment : Fragment() {

    private var _binding: FragmentBreedsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BreedsViewModel by viewModels()
    private val adapter = DogBreedsAdapter(
        onClick = { breed ->
//            viewModel.onBreedClick(breed)
        },
        onImageRequest = { breed ->
            viewModel.loadBreedImage(breed)
        }
    )
    private var isSearchVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreedsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("💡 BreedsFragment onViewCreated")
        setupToolbar()
        setupRecyclerView()
        observeUiState()
        setupRetry()
        setupSearchField()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_search) {
                toggleSearch()
                true
            } else false
        }
    }

    private fun toggleSearch() {
        isSearchVisible = !isSearchVisible
        if (!isSearchVisible) {
            viewModel.updateSearchQuery("")
            binding.searchField.text?.clear()
        }
        binding.searchInputLayout.isVisible = isSearchVisible
        binding.toolbar.menu.findItem(R.id.action_search)
            .setIcon(
                if (isSearchVisible) R.drawable.ic_clear else R.drawable.ic_search
            )
    }

    private fun setupSearchField() {
        binding.searchField.addTextChangedListener { editable ->
            viewModel.updateSearchQuery(editable.toString())
        }
    }

    private fun setupRecyclerView() {
        viewModel.filteredBreeds.observe(viewLifecycleOwner) { list ->
            Timber.d("💡 filteredBreeds size = ${list.size}")
            adapter.submitList(list)
        }

        viewModel.breedImages.observe(viewLifecycleOwner) { images ->
            adapter.updateImages(images)
        }

        binding.recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.adapter = adapter
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.observe(viewLifecycleOwner) { state ->
                Timber.d("💡 uiState = $state")
                binding.loadingView.isVisible = state is UiState.Loading
                binding.errorContainer.isVisible = state is UiState.Error

                when (state) {
                    is UiState.Success -> {
                        adapter.submitList(state.data)
                        binding.recyclerView.isVisible = true
                    }

                    else -> {
                        binding.recyclerView.isVisible = false
                    }
                }

                if (state is UiState.Error) {
                    binding.errorText.text = state.throwable.localizedMessage
                }
            }
        }


    }

    private fun setupRetry() {
        binding.retryButton.setOnClickListener { viewModel.retry() }
    }

    private fun buildAlphabetSidebar(breeds: List<DogBreed>) {
        val letters = breeds.map { it.displayName.first().uppercaseChar() }
        binding.alphabetSidebar.removeAllViews()
        letters.forEach { letter ->
            val tv = TextView(requireContext()).apply {
                text = letter.toString()
                setPadding(4, 8, 4, 8)
                setOnClickListener {
                    adapter.getSectionPosition(letter)?.let { pos ->
                        binding.recyclerView.scrollToPosition(pos)
                    }
                }
            }
            binding.alphabetSidebar.addView(tv)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}