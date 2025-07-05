package io.bossdogs.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import io.bossdogs.R
import io.bossdogs.databinding.FragmentSplashBinding


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

        binding.splashContainer.alpha = 0f
        binding.splashContainer.animate()
            .alpha(1f)
            .setDuration(500)
            .withStartAction { binding.splashAnimation.playAnimation() }
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    BreedsFragment(),
                    "BREEDS"
                )
                .commit()
        }, SPLASH_DELAY_MS)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}