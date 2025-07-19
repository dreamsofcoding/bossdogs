package io.bossdogs.ui.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.bossdogs.MainActivity.Companion.LIST
import io.bossdogs.R
import io.bossdogs.databinding.FragmentSplashBinding
import io.bossdogs.ui.list.BreedsFragment


private const val SPLASH_DELAY_MS = 3000L

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commenceSplashAnimation()
        setupToolbar()
    }

    private fun setupToolbar() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            hide()
        }
    }

    private fun commenceSplashAnimation() {
        binding.splashContainer.apply {
            alpha = 0f
        }

        binding.splashContainer.animate()
            .alpha(1f)
            .setDuration(500)
            .withStartAction { binding.splashAnimation.playAnimation() }
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
            navigateToListScreen()
        }, SPLASH_DELAY_MS)
    }

    private fun navigateToListScreen() {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragment_container,
                BreedsFragment(),
                LIST
            )
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}