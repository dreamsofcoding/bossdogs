package io.bossdogs

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.bossdogs.ui.SplashFragment
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.d("MainActivity created")
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, SplashFragment(), SPLASH)
            .commit()
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