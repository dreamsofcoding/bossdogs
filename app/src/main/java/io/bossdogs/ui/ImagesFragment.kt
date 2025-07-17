package io.bossdogs.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import io.bossdogs.MainActivity
import io.bossdogs.MainActivity.Companion.BREED
import io.bossdogs.R
import io.bossdogs.databinding.FragmentImagesBinding

@AndroidEntryPoint
class ImagesFragment : Fragment() {

    private var _binding: FragmentImagesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ImagesViewModel by viewModels()
    private lateinit var adapter: ImagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagesBinding.inflate(
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

        val breedName = requireArguments().getString(BREED)!!
        viewModel.loadImages(breedName)

        setupToolbar()

        setupAdapter()
    }

    private fun setupAdapter() {
        adapter = ImagesAdapter { image ->
            viewModel.selectHero(image)
        }
        binding.imagesRecyclerview.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.imagesRecyclerview.adapter = adapter
    }

    private fun setupToolbar() {
        (activity as MainActivity).setImagesToolbar(this)
        setupToolbarBackNav()
    }

    private fun setupToolbarBackNav() {
        (activity as AppCompatActivity).findViewById<MaterialToolbar>(R.id.toolbar)
            .setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}