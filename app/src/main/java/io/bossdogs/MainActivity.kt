package io.bossdogs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import dagger.hilt.android.AndroidEntryPoint
import io.bossdogs.ui.image.ImagesFragment
import io.bossdogs.ui.list.BreedsFragment
import io.bossdogs.ui.splash.SplashFragment
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("MainActivity created")
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        setToolbarListener()

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SplashFragment(), SPLASH)
            .commit()
    }

    private fun setToolbarListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            val frag = supportFragmentManager
                .findFragmentById(R.id.fragment_container)
            when (frag) {
                is BreedsFragment -> {
                    setBreedsToolbar()
                }

                is ImagesFragment -> {
                    setImagesToolbar(frag)
                }
            }
        }

    }

    fun setBreedsToolbar() {
        supportActionBar?.apply {
            show()
            title = getString(R.string.app_name)
            setDisplayHomeAsUpEnabled(false)
        }
    }

    fun setImagesToolbar(frag: ImagesFragment) {
        val breed = frag.requireArguments().getString(BREED)!!
        supportActionBar?.apply {
            show()
            title = breed.replaceFirstChar { it.uppercase() }
            setDisplayHomeAsUpEnabled(true)
        }
        findViewById<MaterialToolbar>(R.id.toolbar)
            .navigationIcon
            ?.setTint(ContextCompat.getColor(this, R.color.white))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        Timber.d("MainActivity destroyed")
    }

    companion object {
        val SPLASH = "splash"
        val LIST = "list"
        val IMAGES = "images"
        val BREED = "breed"
    }
}