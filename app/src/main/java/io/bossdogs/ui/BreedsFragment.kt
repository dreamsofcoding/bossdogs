package io.bossdogs.ui

import android.content.res.Resources
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
        setupRecyclerView()
        observeUiState()
        setupRetry()
    }
    private fun setupRecyclerView() {
        viewModel.filteredBreeds.observe(viewLifecycleOwner) { list ->
            Timber.d("💡 filteredBreeds size = ${list.size}")
            adapter.submitList(list)

            val showSidebar = list.isNotEmpty() && viewModel.searchQuery.value.isNullOrBlank()
            binding.alphabetSidebar.isVisible = showSidebar

            if (showSidebar) {
                buildAlphabetSidebar(list)
            }
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

//    private fun buildAlphabetSidebar(breeds: List<DogBreed>) {
//        val letters = breeds.map { it.displayName.first().uppercaseChar() }
//        binding.alphabetSidebar.removeAllViews()
//        letters.distinct().forEach { letter ->
//            val tv = TextView(requireContext()).apply {
//                text = letter.toString()
//                setTextColor(
//                    ContextCompat.getColor(requireContext(), R.color.black)
//                )
//                setPadding(4, 8, 4, 8)
//                setOnClickListener {
//                    adapter.getSectionPosition(letter)?.let { pos ->
//                        binding.recyclerView.scrollToPosition(pos)
//                    }
//                }
//            }
//            binding.alphabetSidebar.addView(tv)
//        }
//    }

    private fun buildAlphabetSidebar(breeds: List<DogBreed>) {
        val letters = breeds
            .map { it.displayName.first().uppercaseChar() }
            .distinct()
            .sorted()                 // optional: ensure A→Z order

        val sidebar = binding.alphabetSidebar
        sidebar.removeAllViews()

        // Tell the LinearLayout how many “weight units” it will distribute:
        sidebar.weightSum = letters.size.toFloat()

        letters.forEach { letter ->
            val tv = TextView(requireContext()).apply {
                text = letter.toString()
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                gravity = Gravity.CENTER

                // Give each child a height of 0 and weight=1 so they evenly fill the sidebar
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    0,
                    1f
                ).apply {
                    // optional horizontal margin
                    leftMargin = 4.dpToPx()
                    rightMargin = 4.dpToPx()
                }

                setOnClickListener {
                    adapter.getSectionPosition(letter)?.let { pos ->
                        binding.recyclerView.scrollToPosition(pos)
                    }
                }
            }
            sidebar.addView(tv)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

fun Int.dpToPx(): Int =
    (this * Resources.getSystem().displayMetrics.density).toInt()